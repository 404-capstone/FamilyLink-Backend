package capstone._4.service;

import capstone._4.dto.album.S3PhotoInfoDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        }catch (AmazonS3Exception e){
            throw new AmazonS3Exception("삭제에 실패하였습니다"+e.getMessage());
        }
    }

    private String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(fileName);
    }

    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        }catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException("파일명이 잘못되었습니다.");
        }

    }

    public S3PhotoInfoDto uploadFiles(List<MultipartFile> files){
        List<String> fileNames = new ArrayList<>();
        List<String> fileUrls=new ArrayList<>();

        files.forEach(file ->{ //하나씩 파일 꺼내서 처리가능.
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            try(InputStream input= file.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucket,fileName,input,objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }catch (IOException e){
                throw new AmazonS3Exception("저장에 실패했습니다" + e.getMessage());
            }
            fileNames.add(fileName);
            fileUrls.add(amazonS3.getUrl(bucket,fileName).toString());
        });
        return S3PhotoInfoDto.builder()
                .fileNames(fileNames)
                .fileUrls(fileUrls).build();
    }
}
