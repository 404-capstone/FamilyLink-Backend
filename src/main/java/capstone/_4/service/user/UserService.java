package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.SocialInfoDto;
import capstone._4.dto.user.input.SocialInputDto;
import capstone._4.dto.user.output.SocialResultDto;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.token.JwtService;
import capstone._4.util.AESUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
    public SocialResultDto userSave(SocialInputDto socialInputDto) {  //이거 메소드 카카오 로그인 수정하고, 없애기.
        SocialInfoDto socialInfoDto;

        if ("kakao".equalsIgnoreCase(socialInputDto.getProvider())) {
            socialInfoDto = socialService.kakaoLoginService(socialInputDto);  // SocialInputDto 전체 전달
        }  else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }


        String email=aesUtil.encrypt(socialInfoDto.getEmail());

        boolean newuser=false;


        User user=userRepository.findByEmail(email).orElse(null);
        if(user==null) {
            user = new User(socialInfoDto.getSocial(), email, socialInfoDto.getNickname(),null);
            newuser=true;

        }

        userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        //jwttoken edit 하는 로직 추가.
        SocialResultDto socialResultDto = new SocialResultDto(user.getId(),
                socialInfoDto.getSocial(),newuser ,accessToken, refreshToken);
        return socialResultDto;
    }

    @Transactional
    public User saveUserV2(User user){
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User findById(int id){
        Optional<User> user=userRepository.findById(id);
        return user.orElse(null); //여기 나중에 리팩토링.
    }

    public User findByEmail(String email) {
        Optional<User> user= userRepository.findByEmail(email);
        return user.orElse(null);
    }
}
