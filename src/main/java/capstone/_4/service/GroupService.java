package capstone._4.service;

import capstone._4.domain.Groups;
import capstone._4.domain.GroupsUser;
import capstone._4.domain.User;
import capstone._4.dto.album.S3PhotoInfoDto;
import capstone._4.dto.group.input.ServeyDto;
import capstone._4.dto.group.output.GroupInfoResponseDto;
import capstone._4.dto.group.output.GroupGenerateDto;
import capstone._4.dto.group.output.GroupUserInfoDto;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.group.GroupsUserReponsitory;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.redis.RedisService;
import capstone._4.util.ImageHandler;
import com.soundicly.jnanoidenhanced.jnanoid.NanoIdUtils;
import io.lettuce.core.RedisException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupsUserReponsitory groupsUserReponsitory;
    private final RedisService redisService;
    private final ImageHandler imageHandler;
    private final S3Service s3Service;


/**
 * 그룹 생성과 리더 설정을 수행.
 */

    @Transactional
    public GroupGenerateDto generateGroup(String name, int id,String role,MultipartFile image) {
        User user = getUserFromId(id);
        S3PhotoInfoDto s3PhotoInfoDto =s3Service.uploadFile(image);
        //String path = checkImage(image);
        Groups groups=new Groups(name,s3PhotoInfoDto.getFileUrl(),s3PhotoInfoDto.getFileName());
        groupRepository.save(groups);
        GroupsUser groupsuser=new GroupsUser(groups,user,role,true);
        groupsUserReponsitory.save(groupsuser);
        return new GroupGenerateDto(groups.getGroup_name(),groups.getGup_id());
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
        List<GroupUserInfoDto> users=groupsUserReponsitory.findBygroupId(groupid);
        return GroupInfoResponseDto.builder()
                .group_name(group.getGroup_name())
                .group_id(groupid)
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
        boolean flag = groupsUserReponsitory.existsByGroupIdAndUserid(groupid,id);
        if(flag){
            throw new EntityExistsException("이미 그룹에 가입했습니다");
        }
        User user = getUserFromId(id);
        GroupsUser groupsUser=new GroupsUser(group,user,role,false);
        groupsUserReponsitory.save(groupsUser);
        return GroupGenerateDto.builder()
                .groupName(group.getGroup_name())
                .groupId(group.getGup_id())
                .build();
    }

    @Transactional
    public void quitGroup(Integer groupId,Integer userid) {
        int count = groupsUserReponsitory.deleteUser(groupId,userid);
        if(count == 0){
            throw new EntityNotFoundException("그룹유저가 삭제 되지 않았음.");
        }
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        int count = groupRepository.deleteGroupe(groupId);
        if(count == 0){
            throw new EntityNotFoundException("그룹 삭제 안됨");
        }
    }

    @Transactional
    public void updateGroup(Integer groupId, String name, MultipartFile image) {
        //String path = checkImage(image);
        S3PhotoInfoDto s3PhotoInfoDto =s3Service.uploadFile(image);

        Groups group =groupRepository.findById(groupId).get();
        s3Service.deleteFile(group.getImage_name());
        Long count= groupRepository.updateGroup(groupId,name,s3PhotoInfoDto.getFileUrl(),s3PhotoInfoDto.getFileName());
        if(count == 0){
            throw new EntityNotFoundException("그룹이 존재하지 않습니다.");
        }
    }

    @Transactional
    public void deleteUserWithGroup(Integer groupId,Integer userid) {
        int count = groupsUserReponsitory.deleteUser(groupId,userid);
        if(count == 0){
            throw new EntityNotFoundException("그룹유저가 삭제 되지 않았음.");
        }
    }

    public String searchCode(Integer groupid) {
        String code=groupRepository.findById(groupid).get().getCode();
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


    private String checkImage(MultipartFile image) {
        if(image !=null){
            return imageHandler.saveImage(image);
        }else{
            return null;
        }
    }

    private GroupsUser getGroupsUser(Integer groupId, int id) {
        return groupsUserReponsitory.findByIds(groupId, id).orElseThrow(
                () -> new EntityNotFoundException("그룹 유저가 존재하지 않습니다.")
        );
    }



}
