package capstone._4.repository.album;

import capstone._4.domain.PhotoImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhotoImageRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveAll(List<PhotoImage> photoImages) {
        for(PhotoImage photoImage:photoImages){
            em.persist(photoImage);
        }
    }

    public List<PhotoImage> findImage(Integer photo_id){ //이미지 가져오기.
        return em.createQuery("select p from PhotoImage p where p.photo.id=:photo_id",PhotoImage.class)
                .setParameter("photo_id",photo_id)
                .getResultList();
    }
}
