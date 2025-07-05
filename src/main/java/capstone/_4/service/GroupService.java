package capstone._4.service;

import capstone._4.domain.User;
import capstone._4.dto.group.GroupResponseDto;
import capstone._4.repository.GroupRepository;
import capstone._4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupResponseDto generateGroup(String name, int id) {
        User user=userRepository.findById(id).get();

    }
}
