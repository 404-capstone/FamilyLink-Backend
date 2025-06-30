package capstone._4.repository;

import capstone._4.domain.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public boolean saveUser(User user){
        em.persist(user);
        return true;
    }


}
