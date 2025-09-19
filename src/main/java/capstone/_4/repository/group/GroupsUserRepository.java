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

    public Optional<GroupsUser> findByUIdAndGupId(int uId, int gupId) {
        return em.createQuery("select gu from GroupsUser gu " +
                        "where gu.user.id = :uId and gu.group.id = :gupId", GroupsUser.class)
                .setParameter("uId", uId)
                .setParameter("gupId", gupId)
                .getResultList().stream().findFirst();
    }
    //유저삭제
    public int deleteUser(Integer groupId,Integer userid) {
        return em.createQuery("delete from GroupsUser gu " +
                "where gu.group.gup_id = :groupid and " +
                "gu.user.id = :userid")
                .setParameter("groupid", groupId)
                .setParameter("userid", userid)
                .executeUpdate();

    }
    //엔티티 삭제
    public void deleteUserWithEm(GroupsUser groupUser){
        em.remove(groupUser);

    }

    // 특정 유저가 속한 그룹의 GroupsUser 엔티티 조회
    public List<GroupsUser> findByUId(int uId) {
        return em.createQuery("select gu from GroupsUser gu where gu.user.id = :uId", GroupsUser.class)
                .setParameter("uId", uId)
                .getResultList();
    }

    // 같은 그룹에서 특정 유저를 제외하고 u_id 오름차순으로 가장 낮은 사람 조회
    public Optional<GroupsUser> findTopByGupIdAndUIdNot(int gupId, int excludedUId) {
        return em.createQuery("select gu from GroupsUser gu " +
                        "where gu.group.gup_id = :gupId and gu.user.id != :excludedUId " +
                        "order by gu.user.id asc", GroupsUser.class)
                .setParameter("gupId", gupId)
                .setParameter("excludedUId", excludedUId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst();
    }

}
