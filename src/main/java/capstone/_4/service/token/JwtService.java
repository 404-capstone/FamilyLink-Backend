package capstone._4.service.token;

import capstone._4.domain.User;
import capstone._4.dto.user.ReGenerateTokenDto;
import capstone._4.exception.TokenException;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.redis.RedisService;
import capstone._4.util.AESUtil;
import capstone._4.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    /**
     * 해당 메소드는, 리프레시 토큰을 통해 액세스 토큰과 리프레시를 재생성 하는 코드로
     * 토큰 재생성 api외 사용하지 않는다.
     * @param refreshToken
     * @return
     */

    public ReGenerateTokenDto generateToken(String refreshToken){
        //로직이 먼저 refresh받고 안에 상태 검증후에,안에 claim꺼내서 id꺼내서 그걸로 user조회한다음에 재생성.
        ReGenerateTokenDto reGenerateTokenDto = new ReGenerateTokenDto();
        if(refreshToken != null && refreshToken.startsWith("Bearer ")){
            String token = refreshToken.substring(7);
            log.info("token: {}",token);
            try{
                if(validateRefreshToken(token)){
                    int id=getIdFromToken(token);
                    log.info("id: {}",id);
                    Optional<User> user=userRepository.findById(id);
                    reGenerateTokenDto.setAccessToken(generateAccessToken(user.orElse(null)));
                    reGenerateTokenDto.setRefreshToken(generateRefreshToken(user.orElse(null)));
                    redisService.delete(token);
                }
            }catch(Exception e){
                throw new TokenException("user가 존재하지 않습니다."+e.getMessage());
            }
        }else{
            throw new TokenException("토큰이없거나 Bearer이 존재하지않습니다.");
        }
        return reGenerateTokenDto;
    }

    /**
     * 리프레시 토큰을 검증하는 메소드.
     * @param refreshToken
     * @return
     */
    private boolean validateRefreshToken(String refreshToken){
        boolean refreshvalid=jwtUtil.getTokenStatus(refreshToken, REFRESH_SECRET_KEY);
        boolean isTokenMatched=false;
        Object id=redisService.getData(refreshToken);
        if(id !=null){
            isTokenMatched=true;
        }else throw new TokenException("해당 토큰은 저장소에 존재하지 않습니다");
        return refreshvalid&&isTokenMatched;
    }

    /**
     * 넘어온 user도메인을 통해서 액세스 토큰을 만들어주는 메소드
     * 로그인시 아래 메소드와 같이 생성해서 반환해준다.
     * 보통 이걸통해서 액세스 토큰을 생성한다
     * @apiNote jwtutil로 유저 정보를 넘겨서,유저 dbid값과 이메일 값을 암호화한다.
     * 2. 암호화후 jwt claim안에 저장하고 jwt액세스 토큰을 반환한다.
     * @param user 도메인 클래스.
     * @return
     */
    public String generateAccessToken(User user){
        String accessToken= jwtUtil.generateAccessToken(ACCESS_SECRET_KEY,ACCESS_EXPIRATION,user);
        return accessToken;
    }

    /**
     * user도메인을 기준으로 리프레시 토큰을 생성한다.
     * 유저 로그인시, 위 액세스토큰 메소드와 함께 해당 메소드를 사용해서 같이 반환한다.
     * @param user 도메인 클래스
     * @return
     */
    public String generateRefreshToken(User user){
        String refreshToken= jwtUtil.generateRefreshToken(REFRESH_SECRET_KEY,REFRESH_EXPIRATION,user);
        redisService.saveJwt(refreshToken, user.getId());
        return refreshToken;
    }

    /**
     * 이메일을 통해서 jwt토큰을 이용해 인가를 체크하는 메소드
     * @param username
     * @return
     */

    public UsernamePasswordAuthenticationToken getAuthentication(String username){
        UserDetails principal=customUserdetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(principal,"",principal.getAuthorities());
    }

    /**
     * 토큰상태를 체크하는 메소드.
     * @param token
     * @return
     */
    public boolean checkTokenState(String token){
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY);
    }


    private Integer getIdFromToken(String token){
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
        return cli.get("email",String.class); //aESUtil.decrypt();
    }

    public int returnToken(String token){
        if(token.startsWith("Bearer ")){
            String accessToken=token.substring(7);
            int id=getIdFromToken(accessToken);
            return id;
        }else{
            throw new TokenException("토큰 번호가 잘못되었습니다");
        }
    }
}
