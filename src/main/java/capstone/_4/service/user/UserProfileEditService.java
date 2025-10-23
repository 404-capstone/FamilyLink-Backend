package capstone._4.service.user;

import capstone._4.domain.User;
import capstone._4.dto.user.ProfileEditDto;
import capstone._4.dto.album.S3PhotoInfoDto;
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

    private final S3Service s3Service;
    private final capstone._4.repository.user.UserRepository userRepository;

    @Transactional
    public void updateProfile(int userId, ProfileEditDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 텍스트 정보 업데이트
        user.updateProfile(dto.getUsername(), dto.getAge(), dto.getGender());

        MultipartFile imageFile = dto.getImageFile();
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // 새 이미지 업로드
                S3PhotoInfoDto uploadResult = s3Service.uploadFile(imageFile);
                log.info("uploadResult.getFileUrl() -> {}", uploadResult.getFileUrl());
                // 기존 이미지 삭제
                checkUserImage(user);

                // 새 이미지 반영
                user.setImage(uploadResult.getFileUrl());
                log.info("새 프로필 이미지 업로드 완료 -> {}", uploadResult.getFileUrl());

            } else {
                // 이미지 없으면 기존 이미지 삭제 + null 처리
                checkUserImage(user);
                user.setImage(null);
            }
        } catch (Exception e) {
            throw new RuntimeException("S3 업로드/삭제 실패: " + e.getMessage());
        }
    }

    private void checkUserImage(User user) {
        String previousImageUrl = user.getImageUrl();
        if (previousImageUrl != null && !previousImageUrl.isEmpty()) {
            String fileName = previousImageUrl.substring(previousImageUrl.lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
            log.info("기존 프로필 이미지 S3 삭제 -> {}", fileName);
        }
    }
}
