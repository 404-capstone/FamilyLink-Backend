package capstone._4.repository.group;

import capstone._4.domain.Groups;
import capstone._4.domain.QGroups;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupRepository {

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public boolean save(Groups groups) {
        em.persist(groups);
        return true;
    }

    public Optional<Groups> findById(int groupid) {
        Optional<Groups> groups = Optional.ofNullable(em.find(Groups.class, groupid));
        return groups;
    }


    public int deleteGroupe(Integer groupId) {
        return em.createQuery("delete from Groups g " +
                        "where g.gup_id = :groupId")
                .setParameter("groupId", groupId)
                .executeUpdate();
    }

<<<<<<< HEAD:src/main/java/capstone/_4/repository/GroupRepository.java

    public Long updateGroup(Integer groupId,String name ,String imagePath) {
       QGroups qGroups = QGroups.groups;
       JPAUpdateClause update = jpaQueryFactory.update(qGroups);
=======
    public Long updateGroup(Integer groupId,String name ,String imagePath,String imageName) {
        QGroups qGroups = QGroups.groups;
        JPAUpdateClause update = jpaQueryFactory.update(qGroups);
>>>>>>> cc2d5996a4dfd7c7daa82f73d60063b1d0b176fb:src/main/java/capstone/_4/repository/group/GroupRepository.java

        if(StringUtils.hasText(name)){
            update.set(qGroups.group_name,name);
        }
        if (imagePath != null) {
            update.set(qGroups.image,imagePath);
            update.set(qGroups.image_name, imageName);
        }
        return update.where(qGroups.gup_id.eq(groupId))
                .execute();
    }
}
