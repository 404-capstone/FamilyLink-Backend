package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileEditService {

    private final UserRepository userRepository;

    @Transactional
    public User updateProfile(int userId, ProfileEditDto dto) {
        log.info("[서비스 호출] updateProfile userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        log.info("기존 사용자 정보 - username: {}, gender: {}, age: {}", user.getUsername(), user.getGender(), user.getAge());

        user.updateProfile(dto.getUsername(), dto.getAge(), dto.getGender());

        log.info("변경 후 사용자 정보 - username: {}, gender: {}, age: {}", user.getUsername(), user.getGender(), user.getAge());

        return user;
    }
}
