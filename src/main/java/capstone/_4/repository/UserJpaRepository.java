package capstone._4.repository;

import capstone._4.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserJpaRepository implements UserRepository {

    @PersistenceContext
    private final EntityManager em;

    public UserJpaRepository(EntityManager em) {
        this.em = em;
    }

    public boolean save(User user){
        em.persist(user);
        return true;
    }


}
