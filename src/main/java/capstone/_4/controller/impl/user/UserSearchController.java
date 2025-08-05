package capstone._4.controller.impl.user;

import capstone._4.dto.user.output.UserSearchOutputDto;
import capstone._4.service.token.JwtService;
import capstone._4.controller.doc.UserSearchApi;
import capstone._4.service.user.UserSearchService;
import capstone._4.dto.ApiResponseDto;
import capstone._4.enums.ResponseEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

import capstone._4.domain.User;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserSearchController implements UserSearchApi {

    private final UserSearchService userSearchService;
    private final JwtService jwtService;

    /**
     * 클라이언트가 자신의 정보를 요청할 때 호출되는 메서드
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 읽어 사용자 ID 추출 후
     * 해당 ID로 DB에서 사용자 정보 조회 및 반환
     */

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
}