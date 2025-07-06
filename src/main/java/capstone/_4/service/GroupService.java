package capstone._4.service;

import capstone._4.domain.Groups;
import capstone._4.domain.GroupsUser;
import capstone._4.domain.User;
import capstone._4.dto.group.GroupInfoDto;
import capstone._4.dto.group.GroupGenerateDto;
import capstone._4.dto.group.GroupUserInfoDto;
import capstone._4.repository.GroupRepository;
import capstone._4.repository.GroupsUserReponsitory;
import capstone._4.repository.UserRepository;
import capstone._4.service.redis.RedisService;
import com.soundicly.jnanoidenhanced.jnanoid.NanoIdUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupsUserReponsitory groupsUserReponsitory;
    private final RedisService redisService;


/**
 * 그룹 생성과 리더 설정을 수행.
 */

    @Transactional
    public GroupGenerateDto generateGroup(String name, int id,String role) {
        User user=userRepository.findById(id).get();
        Groups groups=new Groups(name);
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
        Groups group= groupRepository.findById(group_id).get();
        String id = NanoIdUtils.randomNanoId(6);
        redisService.saveCode(id, group.getGup_id());
        group.setCode(id);
        return id;

    }

    //그룹 정보 조회 그룹원까지
    public GroupInfoDto searchGroup(Integer groupid) {
        Groups group=groupRepository.findById(groupid).get();
        List<GroupUserInfoDto> users=groupsUserReponsitory.findBygroupId(groupid);
        return GroupInfoDto.builder()
                .group_name(group.getGroup_name())
                .group_id(groupid)
                .userinfo(users)
                .build();
    }

    @Transactional
    public int searchGroupWithCode(String code) {
        Integer groupid=(Integer) redisService.getData(code);
        if(groupid==null){
            throw new RuntimeException("코드가 존재하지 않습니다.");
        }
        Groups group=groupRepository.findById(groupid).get();
        return group.getGup_id();
    }

    //실제 가입
    @Transactional
    public GroupGenerateDto accessGroupWithCode(String code,int id,String role) {
        Integer groupid=(Integer) redisService.getData(code);
        if(groupid==null){
            throw new RuntimeException("코드가 존재하지 않습니다.");
        }
        Groups group=groupRepository.findById(groupid).get();
        boolean flag = groupsUserReponsitory.existsByGroupIdAndUserid(groupid,id);
        if(flag){
            throw new RuntimeException("이미 그룹에 가입했습니다");
        }
        User user=userRepository.findById(id).get();
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
            throw new RuntimeException("삭제가 되지 않았음.");
        }
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        int count = groupRepository.deleteGroupe(groupId);
        if(count == 0){
            throw new RuntimeException("그룹 삭제 안됨");
        }
    }
}
