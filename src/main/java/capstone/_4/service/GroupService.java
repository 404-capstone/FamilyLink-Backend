package capstone._4.service;

import capstone._4.domain.*;
import capstone._4.dto.album.S3PhotoInfoDto;
import capstone._4.dto.group.input.ServeyDto;
import capstone._4.dto.group.output.GroupInfoResponseDto;
import capstone._4.dto.group.output.GroupGenerateDto;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.exception.GroupException;
import capstone._4.repository.calendar.CalendarRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.schedule.ScheduleRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.other.AlarmService;
import capstone._4.service.other.S3Service;
import capstone._4.service.redis.RedisService;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.soundicly.jnanoidenhanced.jnanoid.NanoIdUtils;
import io.lettuce.core.RedisException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final CalendarRepository calendarRepository;
    private final ScheduleRepository scheduleRepository;
    private final RedisService redisService;
    private final S3Service s3Service;
    private final QuestionService questionService;
    private final AlarmService alarmService;

/**
 * 그룹 생성과 리더 설정을 수행.
 */

    @Transactional
    public GroupGenerateDto generateGroup(String name, int id,String role,MultipartFile image) {
        User user = getUserFromId(id);
        Groups groups = checkImage(name, image);

        groupRepository.save(groups);
        Calendar calendar=new Calendar(groups.getGroup_name());
        groups.changeCalendar(calendar);
        calendarRepository.save(calendar);
        GroupsUser groupsuser=new GroupsUser(groups, user, role,true);
        groupsUserRepository.save(groupsuser);
        alarmService.createTopic(groups, user);
        log.info("그룹 질문 랜덤생성.");
        questionService.generateQuestion(groups);
        return new GroupGenerateDto(groups.getGroup_name(),groups.getGup_id());
    }




    private Groups checkImage(String name, MultipartFile image) {
        Groups groups;
        if(image !=null && !image.isEmpty()){
            try {
                S3PhotoInfoDto s3PhotoInfoDto = s3Service.uploadFile(image);
                groups = new Groups(name, s3PhotoInfoDto.getFileUrl(), s3PhotoInfoDto.getFileName());
            }catch (Exception e){
                throw new AmazonS3Exception("s3저장 실패."+e.getMessage());
            }
        }else{
            groups=new Groups(name);
        }
        return groups;
    }

    /**
     * 초대 코드를 생성하는 메소드
     * @apiNote 1.코드를 생성할 그룹을 찾는다
     * 2. nanoid로 코드를 생성한다
     * 3.redis에서 검증을 위해 저장한다.
     * 4. 유저에게 코드를 전송한다.
     * */
    @Transactional
    public String generateCode(Integer group_id) {
        Groups group= getGroupFromId(group_id);
        String id = NanoIdUtils.randomNanoId(6);
        redisService.saveCode(id, group.getGup_id());
        group.setCode(id);
        return id;

    }

    //그룹 정보 조회 그룹원까지
    public GroupInfoResponseDto searchGroup(Integer groupid) {
        Groups group= getGroupFromId(groupid);
        List<GroupUserInfoDto> users= groupsUserRepository.findBygroupId(groupid);
        return GroupInfoResponseDto.builder()
                .group_name(group.getGroup_name())
                .group_id(groupid)
                .group_image(group.getImage())
                .userinfo(users)
                .build();
    }

    @Transactional
    public int searchGroupWithCode(String code) {
        Integer groupid=getGroupIdFromRedis(code);
        Groups group= getGroupFromId(groupid);
        return group.getGup_id();
    }

    //실제 가입
    @Transactional
    public GroupGenerateDto accessGroupWithCode(String code,int id,String role) {
        Integer groupid = getGroupIdFromRedis(code);
        Groups group= getGroupFromId(groupid);
        boolean flag = groupsUserRepository.existsByGroupIdAndUserid(groupid,id);
        if(flag){
            throw new GroupException("이미 그룹에 가입했습니다");
        }
        User user = getUserFromId(id);
        if(group.getGroupsuser().stream().anyMatch(groupsUser -> groupsUser.getRole().equals(role))){
            throw new GroupException("그룹 역활이 겹칩니다.");
        }
        GroupsUser groupsUser=new GroupsUser(group,user,role,false);
        groupsUserRepository.save(groupsUser);
        alarmService.createTopic(group,user); //d알람 토픽 저장.
        alarmService.groupAccess(group,user); //알람 전송.
        return GroupGenerateDto.builder()
                .groupName(group.getGroup_name())
                .groupId(group.getGup_id())
                .build();
    }

    @Transactional
    public void quitGroup(Integer groupId,Integer userid) {
        scheduleRepository.deleteScheduleByUserId(userid);
        int count = groupsUserRepository.deleteUser(groupId,userid);
        User user = getUserFromId(userid);
        Groups groups = getGroupFromId(groupId);
        alarmService.quitTopic(user,groups);
        if(count == 0){
            throw new EntityNotFoundException("그룹유저가 삭제 되지 않았음.");
        }
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        alarmService.deleteTopic(groupId);
        int count = groupRepository.deleteGroupe(groupId);
        if(count == 0){
            throw new EntityNotFoundException("그룹 삭제 안됨");
        }
    }

    @Transactional
    public void updateGroup(Integer groupId, String name, MultipartFile image) {
        //String path = checkImage(image);
        Groups groups = groupRepository.findById(groupId).orElseThrow(()->new EntityNotFoundException("그룹이 존재하지 않음."));
        Long count =0L;
        if(image!=null && !image.isEmpty()){ //null 아닐시. 이미지가 존재할시.
            try {
                S3PhotoInfoDto s3PhotoInfoDto = s3Service.uploadFile(image);
                checkGroupImage(groups);
                count= groupRepository.updateGroup(groupId,name,s3PhotoInfoDto.getFileUrl(),s3PhotoInfoDto.getFileName());
            }catch (Exception e){
                throw new AmazonS3Exception("s3 저장및 삭제 실패."+e.getMessage());
            }
        }else{
            checkGroupImage(groups);
            count= groupRepository.updateGroup(groupId,name,null,null);
        }
        if(count == 0){
            throw new EntityNotFoundException("그룹이 존재하지 않습니다.");
        }
    }

    private void checkGroupImage(Groups groups) {
        String image = groups.getImage();
        if(image!=null && !image.isEmpty()) {
            s3Service.deleteFile(groups.getImage_name());
        }
    }

    @Transactional
    public void deleteUserWithGroup(Integer groupId,Integer userid) {
        scheduleRepository.deleteScheduleByUserId(userid);
        int count = groupsUserRepository.deleteUser(groupId,userid);
        User user = getUserFromId(userid);
        Groups groups = getGroupFromId(groupId);
        alarmService.quitTopic(user,groups);
        if(count == 0){
            throw new EntityNotFoundException("그룹유저가 삭제 되지 않았음.");
        }
    }

    public String searchCode(Integer groupid) {
        String code= groupRepository.findById(groupid).get().getCode();
        Object check=redisService.getData(code);
        if(check != null){
            return code;
        }else{
            throw new RedisException("그룹 초대코드가 만료되었습니다");
        }
    }

    @Transactional
    public void saveScore(ServeyDto dto, int groupid,int id) {
        log.info("score :{} level:{} percent:{}", dto.getLevel(), dto.getScore(), dto.getPercent());
        GroupsUser groupsUser= getGroupsUser(groupid, id);
        groupsUser.setScore(dto.getScore(),dto.getPercent(),dto.getLevel());
    }

    public ServeyDto searchUserScore(Integer groupId, int id) {
        GroupsUser groupsUser= getGroupsUser(groupId, id);
        return ServeyDto.builder()
                .score(groupsUser.getScore())
                .level(groupsUser.getLevel())
                .percent(groupsUser.getPercent())
                .build();
    }

    @Transactional
    public void quitLeader(Integer groupId,Integer leaderId ,Integer userId) {
        GroupsUser oldLeader=getGroupsUser(groupId,leaderId);
        GroupsUser newLeader=getGroupsUser(groupId,userId);
        newLeader.changeLeader(true);
        groupsUserRepository.deleteUserWithEm(oldLeader);
        Groups groups=getGroupFromId(groupId);
        User user=getUserFromId(userId);
        alarmService.quitTopic(user,groups);
        //groupsUserReponsitory.updateUser(groupId,leaderId,userId);
    }

    @Transactional
    public void updateLeader(Integer groupId,Integer leaderId ,Integer userId) {
        GroupsUser oldLeader=getGroupsUser(groupId,leaderId);
        GroupsUser newLeader=getGroupsUser(groupId,userId);
        oldLeader.changeLeader(false);
        newLeader.changeLeader(true);
        //return true;
        //groupsUserReponsitory.updateLeader(groupId,leaderId,userId);
    }


    public int findGroupId(int userId) {
        Groups groups = userRepository.findGroupById(userId)
                .orElseThrow(()-> new EntityNotFoundException("유저가 가입한 그룹이 존재하지 않습니다"));
        return groups.getGup_id();

    }

    /**
     * 00시 1분마다 전체 그룹에 질문을 체크해 생성할지,다음에 또 사용할지 고르는 로직.
     */
    @Scheduled(cron = "0 0 0 * * *",zone = "Asia/Seoul")
    @Async
    public void createGroupQuestion(){
        final int BATCH_SIZE = 100;
        int pagenum=0;
        Page<Groups> groups;
        log.info("그룹 질문 생성 시작.");

        do{
            PageRequest pageRequest = PageRequest.of(pagenum,BATCH_SIZE); //100개씩 페이징해 부르기.
            groups=groupRepository.findAll(pageRequest);
            for(Groups group:groups.getContent()){
                try {
                    questionService.checkQuestions(group);
                }catch(Exception e){
                    log.error("질문생성중 오류발생:{},{}",e.getMessage(),group.getGup_id());
                    //throw new RuntimeException("질문 생성 작업중 문제발생"+e.getMessage()+"{}");
                }
            }

            pagenum++;
        }while(groups.hasNext());
    }


    private Groups getGroupFromId(Integer groupid) {
        return groupRepository.findById(groupid).orElseThrow(
                () -> new EntityNotFoundException("그룹이 존재하지 않습니다.")
        );
    }

    private User getUserFromId(int id) {
        User user=userRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("유저가 존재하지 않습니다.")
        );
        return user;
    }

    private Integer getGroupIdFromRedis(String code) {
        Integer groupid=(Integer) redisService.getData(code);
        if(groupid==null){
            throw new RedisException("코드가 존재하지 않습니다.");
        }
        return groupid;
    }


    private GroupsUser getGroupsUser(Integer groupId, int id) {
        return groupsUserRepository.findByIds(groupId, id).orElseThrow(
                () -> new EntityNotFoundException("그룹 유저가 존재하지 않습니다.")
        );
    }


}
