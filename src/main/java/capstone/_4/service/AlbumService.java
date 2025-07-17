package capstone._4.service;

import capstone._4.domain.Album;
import capstone._4.domain.Groups;
import capstone._4.domain.Photo;
import capstone._4.domain.PhotoImage;
import capstone._4.dto.album.AlbumInputDto;
import capstone._4.dto.album.PhotoResponseDto;
import capstone._4.dto.album.S3PhotoInfoDto;
import capstone._4.repository.AlbumRepository;
import capstone._4.repository.GroupRepository;
import capstone._4.repository.PhotoImageRepository;
import capstone._4.repository.PhotoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;
    private final PhotoImageRepository photoImageRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public PhotoResponseDto addPitcure(AlbumInputDto albumInputDto) {
        S3PhotoInfoDto info=null;
        List<MultipartFile> files=albumInputDto.getFiles();
        List<PhotoImage> photos = new ArrayList<>();
        if(files == null || files.isEmpty()){
            throw new NoSuchElementException("파일이 존재하지 않습니다.");
//        }else if(files.size()== 1){
//            oneUploadFile(files.get(0));
        }else {
            info=uploadFiles(files);  //여러개 저장.
        }
        Album album=getAlbum(albumInputDto); //새 앨범 생성
        log.info("album={}",album.getId());
        Photo photo=new Photo(albumInputDto.getDate(),albumInputDto.getArea(), albumInputDto.getContent()); //사진갤러리 생성.
        log.info("photo={}",photo.getId());
        photo.setAlbum(album);
        photoRepository.save(photo);
        album.addPhoto(photo);
        for(int i=0;i<files.size();i++){
            String fileName = info.getFileNames().get(i);
            String fileUrl=info.getFileUrls().get(i);
            PhotoImage photoImage=new PhotoImage(fileName,fileUrl);
            photoImage.setPhoto(photo);
            photos.add(photoImage);
        }
        photo.setPhotoImages(photos);//포토 저장.
        photoImageRepository.saveAll(photos); //이미지 저장


        return PhotoResponseDto.builder()
                .albumId(album.getId())
                .photoId(photo.getId())
                .size(photos.size()).build();
    }

    private Album getAlbum(AlbumInputDto albumInputDto) {
        Integer groupId= albumInputDto.getGroupId();
        Date date= albumInputDto.getDate();
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        Integer year=cal.get(Calendar.YEAR);
        Integer month=cal.get(Calendar.MONTH);
        Album album=albumRepository.findByDate(groupId,year,month)
                .orElseGet(()->{
                    Groups groups=groupRepository.findById(groupId).get();
                    Album newalbum=new Album(year,month,groups);
                    return albumRepository.save(newalbum);
                });
        return album;
    }


//    public String oneUploadFile(MultipartFile file) {  //하나만 삭제 가능.
//        if(file.isEmpty()){
//            return null;
//        }
//        String fileName = createFileName(file.getOriginalFilename());
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(file.getSize());
//        objectMetadata.setContentType(file.getContentType());
//
//        try(InputStream inputStream = file.getInputStream()){
//            amazonS3.putObject(new PutObjectRequest(bucket,fileName,inputStream,objectMetadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        }catch (IOException e){
//            throw new AmazonS3Exception("저장에 실패했습니다" + e.getMessage());
//        }
//        amazonS3.getUrl(bucket,fileName).toString(); //url 주소.
//        return fileName;
//    }

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

    private S3PhotoInfoDto uploadFiles(List<MultipartFile> files){
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
