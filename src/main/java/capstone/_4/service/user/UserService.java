package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.SocialInfoDto;
import capstone._4.dto.user.SocialInputDto;
import capstone._4.dto.user.SocialResultDto;
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
//요청 받거나,서비스이용해서 이후 유저 저장하는 서비스 계층.
public class UserService {
    //저장할때.
    private final UserRepository userRepository;
    private final SocialService socialService;
    private final JwtService jwtService;
    private final AESUtil aesUtil;

    @Autowired
    public UserService(UserRepository userRepository, SocialService socialService,
                       JwtService jwtService,AESUtil aesUtil) {
        this.userRepository = userRepository;
        this.socialService = socialService;
        this.jwtService = jwtService;
        this.aesUtil=aesUtil;
    }

    @Transactional
    public SocialResultDto userSave(SocialInputDto socialInputDto) {
        SocialInfoDto socialInfoDto = socialService.naverLoginService(socialInputDto);//new SocialInfoDto("naver","user2@naver.com","유저2");
        String email=aesUtil.encrypt(socialInfoDto.getEmail());

        User user=userRepository.findByEmail(email).orElse(null);
        if(user==null) {
            user = new User(socialInfoDto.getSocial(), email, socialInfoDto.getNickname(),null);
        }

        userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        //jwttoken edit 하는 로직 추가.
        SocialResultDto socialResultDto = new SocialResultDto(user.getId(),
                socialInfoDto.getSocial(), accessToken, refreshToken);
        return socialResultDto;
    }

    @Transactional
    public User findById(int id){
        Optional<User> user=userRepository.findById(id);
        return user.orElse(null); //여기 나중에 리팩토링.
    }
}
