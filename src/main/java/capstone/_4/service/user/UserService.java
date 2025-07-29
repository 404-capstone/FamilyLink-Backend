package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.output.SocialResultDto;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.token.JwtService;
import capstone._4.util.AESUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AESUtil aesUtil;

    @Autowired
    public UserService(UserRepository userRepository,
                       JwtService jwtService,
                       AESUtil aesUtil) {
        this.userRepository = userRepository;
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
}
