package capstone._4.service.album;

import capstone._4.domain.*;
import capstone._4.dto.PhotoInfoDto;
import capstone._4.dto.album.*;
import capstone._4.repository.album.AlbumRepository;
import capstone._4.repository.album.PhotoImageRepository;
import capstone._4.repository.album.PhotoRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.user.UserRepository;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;
    private final PhotoImageRepository photoImageRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public PhotoResponseDto addPitcure(AlbumInputDto albumInputDto) {
        S3PhotoInfoDto info=null;
        List<MultipartFile> files=albumInputDto.getFiles();
        List<PhotoImage> photos = new ArrayList<>();
        List<Integer> userIds = albumInputDto.getUserId();
        if(files == null || files.isEmpty()){
            throw new NoSuchElementException("파일이 존재하지 않습니다.");
        }else {
            info=s3Service.uploadFiles(files);  //여러개 저장.
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
        for(int j=0;j<userIds.size();j++){
            User user=userRepository.findById(userIds.get(j)).get();
            PhotoUser photoUser=new PhotoUser(user,photo);
            user.addPhotoUser(photoUser);
            photoRepository.savePhotoUser(photoUser);
            photo.addPhotoUser(photoUser);
        }

        return PhotoResponseDto.builder()
                .albumId(album.getId())
                .photoId(photo.getId())
                .size(photos.size()).build();
    }

    @Transactional
    public PhotoInfoResponseDto editPhotoInfo(PhotoEditDto photoEditDto) {
        Integer photoid=photoEditDto.getPhotoid();

        Photo photo=photoRepository.findById(photoid)
                .orElseThrow(()-> new EntityNotFoundException("사진 정보가 존재하지 않습니다."));
        List<PhotoUser> photoUsers=photo.getPhotoUser();
        Set<Integer> users=photoUsers.stream()
                .map(pu->pu.getUser().getId())
                .collect(Collectors.toSet());
        Set<Integer> newuser=new HashSet<>(photoEditDto.getUserid());
        Iterator<PhotoUser> iterator=photoUsers.iterator();

        while(iterator.hasNext()){
            PhotoUser photoUser=iterator.next();
            Integer userId=photoUser.getUser().getId();
            if(!newuser.contains(userId)){
                photoRepository.deleteUser(photoUser);
                iterator.remove();
                photo.removeUser(photoUser);
            }
        }
//        for(PhotoUser photoUser:photoUsers){ //여기서 기존 id가 포함되지 않을시.
//            Integer userId=photoUser.getUser().getId(); //순회중에 리스트 수정을 하면안됨.
//            if(!newuser.contains(userId)){
//                photoRepository.deleteUser(photoUser);
//                photo.removeUser(photoUser);
//            }
//        }

        for(Integer userId:newuser){
            if(!users.contains(userId)){
                User user=userRepository.findById(userId)
                        .orElseThrow(()->new EntityNotFoundException("유저가 존재하지 않습니다."));
                PhotoUser photoUser=new PhotoUser(user,photo);
                photo.addPhotoUser(photoUser);
                photoRepository.savePhotoUser(photoUser);
            }
        }
        photo.editInfo(photoEditDto.getTitle(),photoEditDto.getDate(),
                photoEditDto.getArea(),photoEditDto.getContent());
        return PhotoInfoResponseDto.builder()
                .photoid(photo.getId())
                .title(photo.getTitle())
                .date(photo.getDate())
                .content(photo.getContent())
                .userIds(photo.getPhotoUser().stream().map(pu->pu.getUser().getId())
                        .collect(Collectors.toList())).build();
    }

    public void deletePhoto(Integer groupId, Integer photoId) {
        Integer count=photoRepository.deletePhotoById(photoId);
        if(count <=0){
            throw new EntityNotFoundException("사진이 존재하지 않습니다.");
        }
    }

    public AlbumInfoResponseDto searchAlbum(Integer groupId) {
        QAlbum album=QAlbum.album;
        List<Tuple> albumInfo=albumRepository.searchAlbums(groupId);
        List<AlbumInfoDto> albumInfoDtoList=albumInfo.stream()
                .map(t ->{
                    List<PhotoInfoDto> photoInfoDtoList=new ArrayList<>();
                    photoInfoDtoList=photoRepository.searchPhotoWithGroup(t.get(album.id));
                    String date=String.format("04d-02d",t.get(album.year),t.get(album.month));
                    return AlbumInfoDto.builder().
                            date(date)
                            .photoInfoDtoList(photoInfoDtoList)
                    .build();
                }).toList();

        return AlbumInfoResponseDto.builder()
                .groupId(groupId).albumInfoDtoList(albumInfoDtoList).build();
    }

    private Album getAlbum(AlbumInputDto albumInputDto) {
        Integer groupId= albumInputDto.getGroupId();
        LocalDateTime date= albumInputDto.getDate();
        Integer year=date.getYear();
        Integer month=date.getMonthValue();
        Album album=albumRepository.findByDate(groupId,year,month)
                .orElseGet(()->{
                    Groups groups=groupRepository.findById(groupId).get();
                    Album newalbum=new Album(year,month,groups);
                    return albumRepository.save(newalbum);
                });
        return album;
    }



}
