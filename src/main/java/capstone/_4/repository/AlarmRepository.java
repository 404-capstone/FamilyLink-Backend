package capstone._4.repository;

import capstone._4.domain.Alarm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AlarmRepository {

    @PersistenceContext
    private final EntityManager em;

    public void save(Alarm alarm) {
        em.persist(alarm);
    }

    public Optional<Alarm> findByUserId(Integer userId){
        return em.createQuery("select a from Alarm a " +
                "where a.user.id = :userId", Alarm.class)
                .setParameter("userId",userId)
                .getResultList().stream().findFirst();
    }

    public Optional<Alarm> findByUse
}
