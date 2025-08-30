package capstone._4.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditDto {

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "사용자 나이대", example = "30")
    private Integer age;

    @Schema(description = "사용자 성별", example = "남성")
    private String gender;

    @Schema(description = "프로필 사진 URL", example = "https://example.com/profile.jpg")
    private String image; // DB에 저장될 URL

    @Schema(description = "업로드할 프로필 이미지 파일", type = "string", format = "binary")
    private MultipartFile imageFile;

    @Schema(description = "이미지 삭제 요청 여부", example = "false")
    private String deleteImage; // String으로 변경

    // String 값을 boolean으로 변환
    public boolean isDeleteImage() {
        if (this.deleteImage == null) return false;
        return "true".equalsIgnoreCase(this.deleteImage.replace("\"",""));
    }

    // String 필드 Setter
    public void setDeleteImage(String deleteImage) {
        this.deleteImage = deleteImage;
    }

    public String getImageUrl() {
        return this.image;
    }

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
    }
}
