package capstone._4.repository.group;

import capstone._4.domain.Groups;
import capstone._4.domain.QGroups;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionList;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GroupRepository {

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public boolean save(Groups groups) {
        if(groups.getId()==null){
            em.persist(groups);
        }else{
            em.merge(groups);
        }
        return true;
    }

    public void saveQuestion(GroupQuestion groupQuestion) {
        if(groupQuestion.getId()==null){
            em.persist(groupQuestion);
        }else{
            em.merge(groupQuestion);
        }

    }

    public Optional<Groups> findById(int groupid) {
        Optional<Groups> groups = Optional.ofNullable(em.find(Groups.class, groupid));
        log.info("그룹:{}",groups.get().getGroup_name());
        return groups;
    }


    public int deleteGroupe(Integer groupId) {
        return em.createQuery("delete from Groups g " +
                        "where g.gup_id = :groupId")
                .setParameter("groupId", groupId)
                .executeUpdate();
    }


    public Long updateGroup(Integer groupId,String name ,String imagePath,String imageName) {
        QGroups qGroups = QGroups.groups;
        JPAUpdateClause update = jpaQueryFactory.update(qGroups);

        if(StringUtils.hasText(name)){
            update.set(qGroups.group_name,name);
        }
        if (imagePath != null) {
            update.set(qGroups.image,imagePath);
            update.set(qGroups.image_name, imageName);
        }
        return update.where(qGroups.gup_id.eq(groupId))
                .execute();
    }

    public QuestionList RandomSearchList(){
        List<QuestionList> list= em.createQuery("select ql from QuestionList ql", QuestionList.class)
                .getResultList();
        Random random = new Random();
        QuestionList questionList = list.get(random.nextInt(list.size()));

        return questionList;
    }


}
