package capstone._4.controller.impl.user;

import capstone._4.controller.doc.UserApi;
import capstone._4.dto.ApiResponseDto;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.dto.user.output.UserSearchOutputDto;
import capstone._4.enums.ErrorCode;
import capstone._4.enums.ResponseEnum;
import capstone._4.service.redis.RedisService;
import capstone._4.service.user.UserProfileEditService;
import capstone._4.service.user.UserService;
import capstone._4.service.user.UserSearchService;
import capstone._4.service.token.JwtService;
import capstone._4.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Key;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController implements UserApi {

    private final UserService userService;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserProfileEditService userEditService;
    private final UserSearchService userSearchService;


    @Value("${jwt.access-secret}")
    private String ACCESS_SECRET;

    private Key accessKey;

    @PostConstruct
    public void init() {
        accessKey = jwtUtil.generateSigningKey(ACCESS_SECRET);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        int userId = 0;
        try {
            // 1. Authorization 헤더 가져오기 및 유효성 검사
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 없거나 유효하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization 헤더가 없거나 유효하지 않습니다.");
            }


            Integer idFromToken = jwtService.returnToken(authorizationHeader);

            if (idFromToken == null) {
                log.warn("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }
            userId = idFromToken;

            log.info("[회원 탈퇴 요청 시작] userId: {}", userId);

            // 4. UserService에서 실제 회원 탈퇴 처리
            boolean deleted = userService.deleteUserById(userId);
            if (deleted) {
                log.info("[회원 탈퇴 성공] userId: {}", userId);
                return ResponseEntity.ok("회원 탈퇴 성공");
            } else {
                log.warn("[회원 탈퇴 실패 - 유저 없음] userId: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            log.error("[회원 탈퇴 실패] userId: {}, 에러: {}", (userId != 0 ? userId : "알 수 없음"), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @Override
    public ResponseEntity<?> getMyInfo(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 유효하지 않습니다.");
                return ResponseEntity.badRequest().body(
                        new ApiResponseDto<>(400, "Authorization 헤더가 유효하지 않습니다.", null)
                );
            }

            // Bearer 포함된 전체 토큰 그대로 전달
            int userId = jwtService.returnToken(authorizationHeader);

            UserSearchOutputDto dto = userSearchService.findUserDtoById(userId);
            log.info("토큰을 이용한 사용자 조회 성공: ID = {}", userId);

            return ResponseEntity.ok().body(
                    new ApiResponseDto<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), dto)
            );

        } catch (EntityNotFoundException e) {
            log.warn("사용자 정보를 찾을 수 없습니다: {}", e.getMessage());
            return ResponseEntity.status(404).body(
                    new ApiResponseDto<>(404, "사용자 정보를 찾을 수 없습니다.", null)
            );

        } catch (Exception e) {
            log.error("토큰을 이용한 사용자 조회 중 오류 발생: {}", e.getMessage(), e);
            if (e instanceof capstone._4.exception.TokenException) {
                return ResponseEntity.status(401).body(
                        new ApiResponseDto<>(401, "유효하지 않은 토큰입니다.", null)
                );
            }
            return ResponseEntity.internalServerError().body(
                    new ApiResponseDto<>(500, "서버 내부 오류가 발생했습니다.", null)
            );
        }
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더가 없거나 Bearer 토큰 형식이 아닙니다. 로그아웃을 진행합니다.");
            token = authorizationHeader.substring(7);
        }

        if (token != null) {
            // 삭제 전 존재 여부 체크
            boolean existsBefore = redisService.exists(token);
            log.info("토큰 삭제 전 Redis 존재 여부: {}", existsBefore);

            boolean deleted = redisService.delete(token);

            // 삭제 후 존재 여부 체크
            boolean existsAfter = redisService.exists(token);
            log.info("토큰 삭제 후 Redis 존재 여부: {}", existsAfter);

            if (!deleted) {
                log.warn("Redis에서 토큰이 삭제되지 않았습니다. 이미 삭제되었거나 존재하지 않습니다.");
            } else {
                log.info("토큰 삭제 성공");
            }
        } else {
            log.warn("Authorization 헤더가 없거나 Bearer 토큰 형식이 아닙니다.");
        }

        return ResponseEntity.ok("로그아웃 성공");
    }

    @Override
    @PutMapping(value = "info/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editProfile(
            @ModelAttribute ProfileEditDto dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            HttpServletRequest request
    ) {
        int userId = 0;
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization 헤더가 유효하지 않습니다.");
            }

            Integer idFromToken = jwtService.returnToken(authorizationHeader);
            if (idFromToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }
            userId = idFromToken;

            dto.setImageFile(imageFile);
            userEditService.updateProfile(userId, dto);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
