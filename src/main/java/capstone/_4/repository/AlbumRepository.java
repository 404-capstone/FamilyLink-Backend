package capstone._4.repository;

import capstone._4.domain.Album;
import capstone._4.domain.Photo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumRepository {
    @PersistenceContext
    EntityManager em;

    public Album save(Album album){
        em.persist(album);
        return album;
    }

    public Optional<Album> findByDate(Integer group_id, Integer year, Integer month){
        return em.createQuery("select a from Album a where a.groups.id=:group_id " +
                        "and a.year=:year and a.month=:month",Album.class)
                .setParameter("group_id",group_id)
                .setParameter("year",year)
                .setParameter("month",month)
                .getResultList().stream().findFirst();

    }

    public List<Album> findAlbumWithGroup(Integer group_id){ //전체 앨범 정보 가져오기.
        return em.createQuery("select a from Album a where a.groups.id=:group_id",Album.class)
                .setParameter(group_id,group_id)
                .getResultList();
    }

    public boolean existAlbumWithPhoto(Integer group_id, Integer year,Integer month) { //앨범 존재하는지 체크.
        Long count=em.createQuery("select a from Album a where a.groups.id=:group_id " +
                "and a.year=:year and a.month=:month",Long.class)
                .setParameter("group_id",group_id)
                .setParameter("year",year)
                .setParameter("month",month)
                .getSingleResult();
        return count>0;

    }
}
