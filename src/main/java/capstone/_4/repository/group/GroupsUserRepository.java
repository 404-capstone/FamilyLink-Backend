package capstone._4.repository.group;

import capstone._4.domain.GroupsUser;
import capstone._4.dto.group.output.GroupUserInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupsUserRepository {
    @PersistenceContext
    EntityManager em;

    public boolean save(GroupsUser groupsuser){
        em.persist(groupsuser);
        return true;
    }

    public Optional<GroupsUser> findByIds(int groupId, int userId){
       return em.createQuery("select gu from GroupsUser gu " +
                "where gu.group.id = :groupId and gu.user.id = :userId",GroupsUser.class)
                .setParameter("groupId",groupId)
                .setParameter("userId",userId)
                .getResultList().stream().findFirst();


    }

    public List<GroupUserInfoDto> findBygroupId(int groupId) {
        return em.createQuery("select new capstone._4.dto.group.output.GroupUserInfoDto(u.id,u.username,gu.role,u.age,u.image,gu.leader) " +
                "from GroupsUser gu " +
                "join gu.group g " +
                "join gu.user u " +
                "where g.gup_id=:groupId",GroupUserInfoDto.class)
                .setParameter("groupId",groupId)
                .getResultList();

    }

    public boolean existsByGroupIdAndUserid(int groupid,int userid){
        Long count=em.createQuery("select count(gu) from GroupsUser gu " +
                "where gu.group.gup_id=:groupid and gu.user.id=:userid",Long.class)
                .setParameter("groupid",groupid)
                .setParameter("userid",userid)
                .getSingleResult();
        return count > 0;
    }

    public int deleteUser(Integer groupId,Integer userid) {
        return em.createQuery("delete from GroupsUser gu " +
                "where gu.group.gup_id = :groupid and " +
                "gu.user.id = :userid")
                .setParameter("groupid", groupId)
                .setParameter("userid", userid)
                .executeUpdate();

    }

    public void deleteUserWithEm(GroupsUser groupUser){
        em.remove(groupUser);

    }

}
