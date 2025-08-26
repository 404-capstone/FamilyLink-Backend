package capstone._4.repository.album;

import capstone._4.domain.Album;
import capstone._4.domain.Groups;
import capstone._4.domain.QAlbum;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AlbumRepository {
    @PersistenceContext
    EntityManager em;

    private final JPAQueryFactory queryFactory;

    public AlbumRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

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


    public List<Tuple> searchAlbums(Integer groupId) {
        QAlbum album = QAlbum.album;
        return queryFactory.select(album.month,album.year,album.id)
                .from(album)
                .where(album.groups.gup_id.eq(groupId))
                .orderBy(album.year.asc(),album.month.asc())
                .fetch();

//        return em.createQuery("select a from Album a " +
//                "where a.groups.id=:groupId " +
//                        "order by a.year asc , a.month asc",Integer.class)
//                .setParameter("groupId",groupId)
//                .getResultList();
    }

    public Optional<Groups> findGroupByAlbumId(Integer albumid){
        return em.createQuery("select a.groups from Album a " +
                "where a.id=:albumid",Groups.class)
                .setParameter("albumid",albumid)
                .getResultList().stream().findFirst();
    }

    public Optional<Album> findAlbumByPhotoId(Integer photoId){
        return em.createQuery("select p.album from Photo p where p.id=:photoid",Album.class)
                .setParameter("photoid",photoId)
                .getResultList().stream().findFirst();
    }

    public void deleteAlbum(Album album) {
        em.remove(album);
    }
}
