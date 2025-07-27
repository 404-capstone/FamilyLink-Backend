package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.output.UserInfoResponse;
import capstone._4.dto.user.output.UserSearchOutputDto;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        UserSearchOutputDto userDto = new UserSearchOutputDto(user);

        return new UserInfoResponse(userDto);
    }
}
