package capstone._4.repository;


import capstone._4.domain.QGroupsSchedule;
import capstone._4.domain.QSchedule;
import capstone._4.domain.QUser;
import capstone._4.domain.Schedule;
import capstone._4.dto.schedule.GroupScheduleDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ScheduleReposiory {

    @PersistenceContext
    private EntityManager em;

    JPAQueryFactory queryFactory;

    public List<Schedule> getGroupAllSchedules(int groupId){
        return em.createQuery("select s from Schedule s " +
                        "left join s.groupsSchedule gs " +
                        "left join s.calendar c " +
                        "where gs.id is null " +
                        "and c.groups.id = :groupId",Schedule.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    public List<Schedule> getUserSchedules(int userId){
        return em.createQuery("select s from Schedule s " +
                        "left join s.groupsSchedule gs " +
                        "where gs.id is null " +
                        "and s.user.id = :userId",Schedule.class)
                .setParameter("userId", userId)
                .getResultList();
    }


    public List<GroupScheduleDto> getGroupSchedules(int groupid){
        QSchedule schedule= QSchedule.schedule;
        QGroupsSchedule gs= QGroupsSchedule.groupsSchedule;
        QUser user= QUser.user;
        Map<Integer,GroupScheduleDto> dto= queryFactory.from(schedule)  //유저와 그룹스케쥴 정보를 조인하고,
                .join(schedule.groupsSchedule, gs)
                .join(gs.user, user)
                .where(schedule.calendar.groups.gup_id.eq(groupid))  //해당 그룹에 일치하는 정보만 제공한다.
                .transform(GroupBy.groupBy(schedule.id).as(
                        Projections.constructor(  //내가 원하는 dto구조로 다시 만들기.
                                GroupScheduleDto.class
                        , schedule.id,
                                schedule.title,
                                schedule.startTime,
                                schedule.endTime,
                                GroupBy.list(user.id))
                ));
        return new ArrayList<>(dto.values());
    }
}
