package capstone._4.repository;

import capstone._4.domain.GroupsUser;
import capstone._4.dto.group.GroupUserInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupsUserReponsitory {
    @PersistenceContext
    EntityManager em;

    public boolean save(GroupsUser groupsuser){
        em.persist(groupsuser);
        return true;
    }

    public List<GroupUserInfoDto> findBygroupId(int groupid) {
        return em.createQuery("select new capstone._4.dto.group.GroupUserInfoDto(u.username,gu.role,u.age,u.image,gu.leader) " +
                "from GroupsUser gu " +
                "join fetch gu.group g " +
                "join fetch gu.user u " +
                "where g.gup_id=:groupid",GroupUserInfoDto.class).getResultList();

    }

    public int deleteUser(Integer groupId,Integer userid) {
        return em.createQuery("delete from GroupsUser gu " +
                "where gu.group.gup_id = :groupid and " +
                "gu.user.id = :userid")
                .setParameter("groupid", groupId)
                .setParameter("userid", groupId)
                .executeUpdate();

    }
}
