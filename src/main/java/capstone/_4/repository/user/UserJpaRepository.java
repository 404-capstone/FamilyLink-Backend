package capstone._4.repository.user;

import capstone._4.domain.Groups;
import capstone._4.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
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

    public Optional<User> findByUsername(String username) {
        return em.createQuery("select u from User u " +
                "where u.username = :username",User.class)
                .setParameter("username", username)
                .getResultList()
                .stream().findFirst();
    }


    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return em.createQuery("SELECT u From User u " +
                "where u.email=:email",User.class)
                .setParameter("email", email)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public Optional<Groups> findGroupById(int userId) {
        return em.createQuery("select gu.group from GroupsUser gu " +
                "where gu.user.id=:userId",Groups.class)
                .setParameter("userId", userId)
                .getResultList().stream().findFirst();
    }

    @Override
    public List<User> findByIds(List<Integer> usersId) {
        List<User> users=new ArrayList<>();
        for(Integer userId : usersId){
            users.add(em.find(User.class,userId));
        }
        return users;
    }
}
