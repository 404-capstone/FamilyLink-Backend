package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.album.S3PhotoInfoDto;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.repository.user.UserRepository;
import capstone._4.service.other.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileEditService {

    private final UserRepository userRepository;
    private final S3Service s3Service; // S3Service 주입

    @Transactional
    public User updateProfile(int userId, ProfileEditDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 텍스트 정보 업데이트
        user.updateProfile(dto.getUsername(), dto.getAge(), dto.getGender());

        // 이미지 업로드
        MultipartFile imageFile = dto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            S3PhotoInfoDto uploadResult = s3Service.uploadFile(imageFile);
            user.setImage(uploadResult.getFileUrl());
            log.info("프로필 이미지 변경됨 -> {}", uploadResult.getFileUrl());
        }

        return user;
    }
}

