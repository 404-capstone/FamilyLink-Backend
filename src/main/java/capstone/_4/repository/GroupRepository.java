package capstone._4.repository;

import capstone._4.domain.Groups;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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


}
