package capstone._4.repository;

import capstone._4.domain.Album;
import capstone._4.domain.Photo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Transactional
@Repository
public class PhotoRepository {

    @PersistenceContext
    EntityManager em;


    public void save(Photo photo){
        em.persist(photo);
    }

    public List<Photo> findPhotoByAlbum(Integer album_id){
        return em.createQuery("select p from Photo p where p.album.id=:album_id",Photo.class)
                .setParameter("album_id",album_id)
                .getResultList();
    }



}
