package capstone._4.service.user;

import capstone._4.domain.GroupsUser;
import capstone._4.domain.User;
import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.output.SocialResultDto;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.user.UserRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.service.token.JwtService;
import capstone._4.util.AESUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AESUtil aesUtil;
    private final GroupsUserRepository groupsUserRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       GroupsUserRepository groupsUserRepository,
                       JwtService jwtService,
                       AESUtil aesUtil) {
        this.userRepository = userRepository;
        this.groupsUserRepository = groupsUserRepository;
        this.jwtService = jwtService;
        this.aesUtil = aesUtil;
    }

    @Transactional
    public SocialResultDto userSave(String email, String social, String nickname) {
        String encryptedEmail = aesUtil.encrypt(email);
        boolean isNewUser = false;

        User user = userRepository.findByEmail(encryptedEmail).orElse(null);
        if (user == null) {
            user = new User(social, encryptedEmail, nickname, null);
            isNewUser = true;
        }

        userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new SocialResultDto(user.getId(), social, isNewUser, accessToken, refreshToken);
    }

    @Transactional
    public User saveUserV2(User user) {
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // 회원 탈퇴
    @Transactional
    public boolean deleteUserById(int id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        // 속한 그룹 확인 및 leader 권한 이전
        List<GroupsUser> userGroups = groupsUserRepository.findByUId(user.getId());
        for (GroupsUser gu : userGroups) {
            if (Boolean.TRUE.equals(gu.getLeader())) {
                // leader라면 같은 그룹에서 u_id가 가장 낮은 멤버 찾기
                Optional<GroupsUser> newLeaderOpt = groupsUserRepository
                        .findTopByGupIdAndUIdNot(gu.getGroup().getId(), user.getId());
                newLeaderOpt.ifPresent(newLeader -> {
                    newLeader.changeLeader(true);
                    groupsUserRepository.save(newLeader);
                });
            }
        }

        // 유저 삭제
        userRepository.deleteById(id);
        return true;
    }
}
