package capstone._4.repository;

import capstone._4.domain.Groups;
import capstone._4.domain.GroupsUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class GroupRepository {

    @PersistenceContext
    EntityManager em;

    public boolean save(Groups groups) {
        em.persist(groups);
        return true;
    }

    public Optional<Groups> findById(int groupid){
        Optional<Groups> groups=Optional.ofNullable(em.find(Groups.class, groupid));
        return groups;
    }


    public int deleteGroupe(Integer groupId) {
        return em.createQuery("delete from Groups g " +
                "where g.gup_id = :groupId")
                .setParameter("groupId", groupId)
                .executeUpdate();
    }
}
