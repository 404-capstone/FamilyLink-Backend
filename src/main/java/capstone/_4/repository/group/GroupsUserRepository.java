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

//    @Transactional
//    public void updateUser(Integer groupId ,Integer leaderId,Integer userId) {
//
//        int updateQuery=em.createQuery("update GroupsUser gu set gu.leader=true " +
//                        "where gu.group.gup_id=:groupId and gu.user.id=:userId")
//                .setParameter("groupId",groupId)
//                .setParameter("userId",userId)
//                .executeUpdate();
//
//        int deleteQuery=em.createQuery("delete from GroupsUser gu " +
//                "where gu.group.gup_id=:groupId " +
//                        "and gu.user.id=:leaderId")
//                .setParameter("groupId",groupId)
//                .setParameter("leaderId",leaderId)
//                .executeUpdate();
//
//        if(updateQuery<1){
//            throw new EntityNotFoundException("새 그룹장을 찾지못했습니다");
//        }
//        if(deleteQuery<1){
//            throw new EntityNotFoundException("기존 그룹장을 삭제하지 못했습니다.");
//        }
//
//    }

//    @Transactional
//    public void updateLeader(Integer groupId,Integer leaderId, Integer userId) {
//        em.createQuery("update GroupsUser gu " +
//                "set gu.leader=false " +
//                "where gu.group.gup_id=:groupId " +
//                        "and gu.user.id=:leaderId")
//                .setParameter("groupId",groupId)
//                .setParameter("leaderId",leaderId)
//                .executeUpdate();
//
//        em.createQuery("update GroupsUser gu " +
//                "set gu.leader=true " +
//                "where gu.group.gup_id=:groupId " +
//                "and gu.user.id=:userid")
//        .setParameter("groupId",groupId)
//        .setParameter("userid",userId)
//                .executeUpdate();
//
//    }
}
