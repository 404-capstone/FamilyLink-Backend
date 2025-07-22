package capstone._4.repository.schedule;


import capstone._4.domain.QGroupsSchedule;
import capstone._4.domain.QSchedule;
import capstone._4.domain.QUser;
import capstone._4.domain.Schedule;
import capstone._4.dto.schedule.output.GroupScheduleDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ScheduleReposiory {

    @PersistenceContext
    private EntityManager em;

    JPAQueryFactory queryFactory;

    public ScheduleReposiory(JPAQueryFactory queryFactory,EntityManager em) {
        this.queryFactory = queryFactory;
        this.em = em;
    }

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

    public List<GroupScheduleDto> getGroupSchedulesV2(int groupid){
        QSchedule schedule= QSchedule.schedule;
        QGroupsSchedule gs= QGroupsSchedule.groupsSchedule;
        QUser user= QUser.user;
        List<Tuple> result =queryFactory  //해당 그룹과 관련된 것들을 가져온다.
                .select(schedule.id,schedule.title,schedule.startTime,schedule.endTime,user.id)
                .from(schedule)
                .join(schedule.groupsSchedule, gs)
                .join(gs.user, user)
                .where(schedule.calendar.groups.gup_id.eq(groupid))
                .fetch();
        Map<Integer,GroupScheduleDto> dto= new HashMap<>(); //중복 제거를 위해 map사용.

        for(Tuple t:result){
            Integer scheduleId=t.get(schedule.id);
            GroupScheduleDto groupScheduleDto=dto.get(scheduleId);
            if(groupScheduleDto==null){
                groupScheduleDto=new GroupScheduleDto(
                        t.get(schedule.id),
                        t.get(schedule.title),
                        t.get(schedule.startTime),
                        t.get(schedule.endTime),
                        new ArrayList<>()
                );
                dto.put(scheduleId,groupScheduleDto); //새로 생성했으니,이어서 넣음.
            }
            groupScheduleDto.getGroupUserId().add(t.get(user.id));
        }
        return new ArrayList<>(dto.values());

    }

//    private List<Integer> findUserWithSchedule(Integer scheduleid){ //이 부분은 필요없음. 이유는 이미 이너조인하면서, 조건에 충족하는 컬럼도 생성해서 반환해주기 때문이다.
//        QGroupsSchedule groupsSchedule= QGroupsSchedule.groupsSchedule;
//        QUser user= QUser.user;
//        return queryFactory.select(groupsSchedule.user.id).from(groupsSchedule)
//                .where(groupsSchedule.schedule.id.eq(scheduleid)).fetch();
//
//
//    }


}
