package capstone._4.repository.album;

import capstone._4.domain.*;
import capstone._4.domain.photo.*;
import capstone._4.dto.album.output.PhotoInfoDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Repository
public class PhotoRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory jpaQueryFactory;

    public PhotoRepository(EntityManager em,JPAQueryFactory jpaQueryFactory) {
        this.em = em;
        this.jpaQueryFactory=jpaQueryFactory;
    }


    public void save(Photo photo){
        em.persist(photo);
    }

    public void savePhotoUser(PhotoUser photoUser){
        em.persist(photoUser);
    }

    public List<Photo> findPhotoByAlbum(Integer album_id){
        return em.createQuery("select p from Photo p where p.album.id=:album_id",Photo.class)
                .setParameter("album_id",album_id)
                .getResultList();
    }


    public Optional<Photo> findById(Integer photoid) {
        return Optional.ofNullable(em.find(Photo.class, photoid));

    }

    public void deleteUser(PhotoUser photoUser) {
        em.remove(photoUser);
    }

    public Integer deletePhotoById(Integer photoId) {
        return em.createQuery("delete from Photo p " +
                "where p.id=:id")
                .setParameter("id",photoId)
                .executeUpdate();
    }

    public List<PhotoInfoDto> searchPhotoWithGroup(Integer albumId) {
        log.info("사진 여러개 찾기 시작");
        QPhoto photo = QPhoto.photo;
        QPhotoUser photoUser = QPhotoUser.photoUser;
        QPhotoImage photoImage = QPhotoImage.photoImage;

        List<Photo> photos=jpaQueryFactory.selectFrom(photo) //전체 사진만 제공.
                .where(photo.album.id.eq(albumId))
                .fetch();

        log.info("유저별로 두기.");
        Map<Integer,List<Integer>> userMap= jpaQueryFactory //유저 따로 조회
                .select(photoUser.photo.id,photoUser.user.id)
                .from(photoUser)
                .where(photoUser.photo.album.id.eq(albumId))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(photoUser.photo.id), //photoid가 키
                        Collectors.mapping(
                                tuple-> tuple.get(photoUser.user.id)  //그안에 유저아이디가 들어있게 변경
                        , Collectors.toList())

                ));

        Map<Integer,List<String>> imageMap=jpaQueryFactory
                .select(photoImage.photo.id,photoImage.url)//이미지 따로 조회
                .from(photoImage)
                .where(photoImage.photo.album.id.eq(albumId))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple-> tuple.get(photoImage.photo.id),
                        Collectors.mapping(tuple-> tuple.get(photoImage.url)
                        ,Collectors.toList())
                ));


        List<PhotoInfoDto> dto=new ArrayList<>();
        for(Photo p:photos){
            List<Integer> users=userMap.get(p.getId());
            List<String> Images=imageMap.get(p.getId());


            PhotoInfoDto photoInfoDto=new PhotoInfoDto(
                        p.getId(),
                        p.getTitle(),
                        Images.get(0),
                        p.getArea(),
                        p.getContent(),
                        p.getDate(),
                        p.getTime(),
                        users
                );
            dto.add(photoInfoDto);

        }
        return dto;


    }
}
