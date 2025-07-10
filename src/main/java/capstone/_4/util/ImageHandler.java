package capstone._4.util;


import capstone._4.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Component
public class ImageHandler {
    private final String path;

    public ImageHandler(@Value("${path.group}") String path) {
        this.path = path;
    }

    public String saveImage(MultipartFile image) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String name = generateFileName(image);
            Path imagePath = Paths.get(path,name);
            image.transferTo(imagePath);
            return imagePath.toString();
        }catch (IOException e) {
            throw new FileStorageException("이미지 저장에 실패했습니다"+e.getMessage());
        }
    }

    private String generateFileName(MultipartFile image){
        UUID uuid = UUID.randomUUID();
        String name= Optional.ofNullable(image.getOriginalFilename()).orElse("image.png");
        return uuid.toString()+name;
    }
}
