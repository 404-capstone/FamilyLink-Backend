package capstone._4.repository;

import capstone._4.domain.GroupsUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class GroupsUserReponsitory {
    @PersistenceContext
    EntityManager em;

    public boolean save(GroupsUser groupsuser){
        em.persist(groupsuser);
        return true;
    }
}
