package capstone._4.repository.album;

import capstone._4.domain.*;
import capstone._4.dto.PhotoInfoDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.*;

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
                .getResultList().size();
    }

    public List<PhotoInfoDto> searchPhotoWithGroup(Integer albumId) {
        QPhoto photo = QPhoto.photo;
        QPhotoUser photoUser = QPhotoUser.photoUser;
        QPhotoImage  photoImage = QPhotoImage.photoImage;
        List<Tuple> photoList =jpaQueryFactory.select(photo,photoUser.user.id,photoImage.url)
                .from(photo)
                .join(photo.photoUser, photoUser).fetchJoin()
                .join(photo.photoImages,photoImage).fetchJoin()
                .where(photo.album.id.eq(albumId))
                .orderBy(photo.date.asc(),photoImage.id.asc())
                .fetch();

        Map<Integer,PhotoInfoDto> dto = new HashMap<>();

        for(Tuple t:photoList){
            Integer photoId=t.get(photo.id);
            PhotoInfoDto photoInfoDto=dto.get(photoId); //dto에서 기존에 있는거 가져오기
            if(photoInfoDto==null){
                photoInfoDto=new PhotoInfoDto(
                        t.get(photo.id),
                        t.get(photo.title),
                        t.get(photoImage.url),
                        t.get(photo.area),
                        t.get(photo.content),
                        new ArrayList<>()
                );
                dto.put(photoId,photoInfoDto);
            }
            //중복 방지.
            if(photoInfoDto.getThumnailurl()==null||photoInfoDto.getThumnailurl().isEmpty()){
                photoInfoDto.setThumnailurl(t.get(photoImage.url));
            }
            Integer userid=t.get(photoUser.id);
            if(!photoInfoDto.getUserid().contains(userid)){
                photoInfoDto.getUserid().add(userid);
            }

        }
        return new ArrayList<>(dto.values());


    }
}
