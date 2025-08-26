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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileEditService {

    private final S3Service s3Service;
    private final capstone._4.repository.user.UserRepository userRepository;

    @Transactional
    public User updateProfile(int userId, ProfileEditDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 텍스트 정보 업데이트
        user.updateProfile(dto.getUsername(), dto.getAge(), dto.getGender());

        // 이미지 처리
        MultipartFile imageFile = dto.getImageFile();
        String previousImageUrl = user.getImageUrl();
        String imageUrl = previousImageUrl; // 기본값 유지

        boolean deleteRequested = dto.isDeleteImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            // 새 이미지 업로드
            S3PhotoInfoDto uploadResult = s3Service.uploadFile(imageFile);
            imageUrl = uploadResult.getFileUrl();
            log.info("새 프로필 이미지 업로드 완료 -> {}", imageUrl);

            // 기존 이미지 삭제
            if (previousImageUrl != null && !previousImageUrl.isEmpty()) {
                String previousFileName = URLDecoder.decode(
                        previousImageUrl.substring(previousImageUrl.lastIndexOf("/") + 1),
                        StandardCharsets.UTF_8
                );
                s3Service.deleteFile(previousFileName);
                log.info("기존 이미지 S3 삭제 완료 -> {}", previousFileName);
            }

        } else if (deleteRequested) {
            // 삭제 요청 시
            if (previousImageUrl != null && !previousImageUrl.isEmpty()) {
                String previousFileName = URLDecoder.decode(
                        previousImageUrl.substring(previousImageUrl.lastIndexOf("/") + 1),
                        StandardCharsets.UTF_8
                );
                s3Service.deleteFile(previousFileName);
                log.info("이미지 삭제 요청, S3 삭제 완료 -> {}", previousFileName);
            }
            imageUrl = null;
        }

        user.setImage(imageUrl);
        return user;
    }
}
