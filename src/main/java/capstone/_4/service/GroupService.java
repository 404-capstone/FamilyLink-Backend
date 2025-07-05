package capstone._4.service;

import capstone._4.domain.Groups;
import capstone._4.domain.GroupsUser;
import capstone._4.domain.User;
import capstone._4.dto.group.GroupResponseDto;
import capstone._4.repository.GroupRepository;
import capstone._4.repository.GroupsUserReponsitory;
import capstone._4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupsUserReponsitory groupsUserReponsitory;


/**
 * 그룹 생성과 리더 설정을 수행.
 */

    @Transactional
    public GroupResponseDto generateGroup(String name, int id) {
        User user=userRepository.findById(id).get();
        Groups groups=new Groups(name);
        groupRepository.save(groups);
        GroupsUser groupsuser=new GroupsUser(groups,user,true);
        groupsUserReponsitory.save(groupsuser);
        return new GroupResponseDto(groups.getGroup_name(),groups.getGup_id());
    }
}
