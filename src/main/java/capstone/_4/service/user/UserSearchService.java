package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.output.UserSearchOutputDto;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService {

    private final UserRepository userRepository;

    public UserSearchOutputDto findUserDtoById(int id) {
        log.info("사용자 조회 시도: ID = {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다: ID = {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });

        log.info("사용자 조회 성공: ID = {}", id);
        return new UserSearchOutputDto(user);
    }
}
