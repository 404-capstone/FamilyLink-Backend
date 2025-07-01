package capstone._4.service;

import capstone._4.domain.User;
import capstone._4.dto.SocialInfoDto;
import capstone._4.dto.SocialResultDto;
import capstone._4.repository.UserRepository;
import capstone._4.service.token.JwtService;
import capstone._4.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SocialResultDto userSave(String SocialToken) {
        SocialInfoDto socialInfoDto = socialService.naverLoginService(SocialToken);
        String email=aesUtil.encrypt(socialInfoDto.getEmail());

        User user = new User(socialInfoDto.getSocial(), email, socialInfoDto.getNickname());

        userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        //jwttoken edit 하는 로직 추가.
        SocialResultDto socialResultDto = new SocialResultDto(user.getId(),
                socialInfoDto.getSocial(), accessToken, refreshToken);
        return socialResultDto;
    }
}
