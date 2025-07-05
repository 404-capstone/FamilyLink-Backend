package capstone._4.service.token;

import capstone._4.domain.User;
import capstone._4.dto.user.GenerateTokenDto;
import capstone._4.exception.TokenException;
import capstone._4.repository.UserRepository;
import capstone._4.service.redis.RedisService;
import capstone._4.service.user.UserService;
import capstone._4.util.AESUtil;
import capstone._4.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import java.security.Key;
import java.util.Optional;

@Service
@Slf4j
public class JwtService {
    private final CustomUserdetailsService customUserdetailsService;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserRepository userRepository;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private final AESUtil aESUtil;

    public JwtService(CustomUserdetailsService customUserdetailsService,
                      JwtUtil jwtUtil,
                      RedisService redisService,
                      @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
                      @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
                      @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
                      @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION, AESUtil aESUtil,UserRepository userRepository) {
        this.customUserdetailsService = customUserdetailsService;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.userRepository = userRepository;
        this.ACCESS_SECRET_KEY = jwtUtil.generateSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.generateSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
        this.aESUtil = aESUtil;
    }

    public GenerateTokenDto generateToken(String refreshToken){
        //로직이 먼저 refresh받고 안에 상태 검증후에,안에 claim꺼내서 id꺼내서 그걸로 user조회한다음에 재생성.
        GenerateTokenDto generateTokenDto = new GenerateTokenDto();
        if(refreshToken != null && refreshToken.startsWith("Bearer ")){
            String token = refreshToken.substring(7);
            log.info("token: {}",token);
            try{
                if(validateRefreshToken(token)){
                    int id=getIdFromToken(token);
                    log.info("id: {}",id);
                    Optional<User> user=userRepository.findById(id);
                    generateTokenDto.setAccessToken(generateAccessToken(user.orElse(null)));
                    generateTokenDto.setRefreshToken(generateRefreshToken(user.orElse(null)));
                }
            }catch(Exception e){
                throw new TokenException("user가 존재하지 않습니다."+e.getMessage());
            }
        }else{
            throw new TokenException("토큰이없거나 Bearer이 존재하지않습니다.");
        }
        return generateTokenDto;
    }

    private boolean validateRefreshToken(String refreshToken){
        boolean refreshvalid=jwtUtil.getTokenStatus(refreshToken, REFRESH_SECRET_KEY);
        boolean isTokenMatched=false;
        Object id=redisService.getData(refreshToken);
        if(id !=null){
            isTokenMatched=true;
        }else throw new TokenException("해당 토큰은 저장소에 존재하지 않습니다");
        return refreshvalid&&isTokenMatched;
    }

    public String generateAccessToken(User user){
        String accessToken= jwtUtil.generateAccessToken(ACCESS_SECRET_KEY,ACCESS_EXPIRATION,user);
        return accessToken;
    }

    public String generateRefreshToken(User user){
        String refreshToken= jwtUtil.generateRefreshToken(REFRESH_SECRET_KEY,REFRESH_EXPIRATION,user);
        redisService.saveJwt(refreshToken, String.valueOf(user.getId()));
        return refreshToken;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String username){
        UserDetails principal=customUserdetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(principal,"",principal.getAuthorities());
    }

    public boolean checkTokenState(String token){
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY);
    }

    public Integer getIdFromToken(String token){
        Claims cli= Jwts.parserBuilder()
                .setSigningKey(ACCESS_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(aESUtil.decrypt(cli.getSubject()));

    }

    public String getEmailFromToken(String token) {
        Claims cli = Jwts.parserBuilder()
                .setSigningKey(ACCESS_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return aESUtil.decrypt(cli.get("email",String.class));
    }
}
