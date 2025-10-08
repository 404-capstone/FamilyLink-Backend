package capstone._4.service.other;

import capstone._4.domain.Alarm;
import capstone._4.domain.Groups;
import capstone._4.domain.GroupsUser;
import capstone._4.domain.User;
import capstone._4.event.*;
import capstone._4.repository.AlarmRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.group.GroupsUserRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 알람관련 이벤트를 발행하는 클래스. 첫시작 클래스.
 * 사용시 이쪽 클래스와 같이 event 클래스만 추가해서 사용하면된다.(ex. topicNotifyEvent 같은거)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final UserRepository userRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final GroupRepository groupRepository;

    private final AlarmRepository alarmRepository;
    private final ApplicationEventPublisher publisher;

    /**
     * 그룹원들을 묶어주는 topic을 생성하는 메소드. (사용안해도됌)
     * @param groups
     * @param user
     */
    @Transactional
    public void createTopic(Groups groups, User user) {
        if(user.getAlarm()!=null &&user.getAlarm().isEnabled()) {
            String topicname = groups.getTopic_name();
            if (topicname == null || topicname.isBlank()) {
                topicname = "group" + groups.getGup_id();
                groups.changeTopic(topicname);
                log.info("토픽 생성:{}", topicname);
            }

            Alarm alarm = user.getAlarm();
            if (alarm != null && alarm.isEnabled() && alarm.getDevice_token() != null) {
                publisher.publishEvent(new TopicSubscribeEvent(topicname, alarm.getDevice_token()));
            }
        }
    }

    /**
     * 개인사용자에 디바이스 토큰을 fcm에 등록하는 메소드(알람 전송을 위해)
     * @param androidToken
     * @param userId
     */
    @Transactional
    public void tokenSave(String androidToken, Integer userId) {
        log.info("token:{}", androidToken);
        if (androidToken != null) {
            Alarm alarm = alarmRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId).orElseThrow(() ->
                                new EntityNotFoundException("유저를 찾을수 없습니다."));
                        return new Alarm(user);
                    });
            alarm.changeToken(androidToken);
            alarmRepository.save(alarm);
        }
    }


    /**
     * 알람 전송 여부를 on/off하는 메소드
     * @param userId
     * @param flag
     */
    public void changeState(int userId, boolean flag) {
        Alarm alarm = alarmRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("알람 세팅을 찾을수 없습니다."));

        alarm.chageState(flag);

        User user = alarm.getUser();
        Optional<Groups> groups = userRepository.findGroupById(userId);
        if (groups.isEmpty()) return;
        String groupName = groups.get().getTopic_name();
        String userToken = user.getAlarm().getDevice_token();
        if (flag) publisher.publishEvent(new TopicSubscribeEvent(groupName, userToken));//플래그가 true 면. 토픽에 참여하기.
        else publisher.publishEvent(new TopicUnSubscribeEvent(groupName, userToken));//quitTopic(user, groups);
    }

    /**
     * 토픽을 fcm에서 삭제하는 클래스로 그룹 삭제시 사용.
     * @param groupId
     */
    public void deleteTopic(Integer groupId) {
        Groups groups = groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("그룹이 존재하지 않습니다"));
        if (groups.getTopic_name() != null) {
            List<GroupsUser> groupsUsers = groups.getGroupsuser();
            List<String> tokens = groupsUsers.stream().map(GroupsUser::getUser)
                    .map(User::getAlarm).filter(Objects::nonNull)
                    .map(Alarm::getDevice_token).filter(Objects::nonNull).toList(); //null인거 치워버리기.
            if (!tokens.isEmpty()) {
                log.info("토픽 삭제.");
                publisher.publishEvent(new TopicDeleteEvent(groups.getTopic_name(), tokens));
                //fcmService.deleteTopic(groups.getTopic_name(), tokens);
            }
        }

    }

    /**
     * 개인 사용자를 그룹 topic에서 탈퇴시키는 메소드.
     * @param user
     * @param groups
     */
    public void quitTopic(User user, Groups groups) {
        if (user.getAlarm() != null && user.getAlarm().isEnabled()) {
            log.info("토픽에서 제거.");
            String token = user.getAlarm().getDevice_token();
            publisher.publishEvent(new TopicUnSubscribeEvent(groups.getTopic_name(), token));
        }

    }

    /**
     * 그룹 가입시 알람 전송 이벤트를 발생시키는 메소드
     * @param group
     * @param role
     */
    public void groupAccess(Groups group, String role) {
        if(group.getTopic_name()!=null) { //topic이 있을때만 사용
            log.info("그룹 가입 메시지 전송.");
            Map<String,String> body = new HashMap<>(); //map형태로 전송해서 여러 데이터를 담게 작성.
            body.put("role",role);
            //이벤트 발생.그룹원 전체에게 알림.
            publisher.publishEvent(new TopicNotifyAllEvent("그룹 가입","group-1",body,group.getTopic_name()));
        }
    }

    /**
     * 그룹원 추방시 이벤트를 발생시키는 이벤트.
     * @param user
     */
    public void groupUserDelete(User user){
        if(user.getAlarm()!=null&&user.getAlarm().isEnabled()) {
            Map<String,String> body = new HashMap<>();
            body.put("message","가족 그룹에서 추방되었습니다.");
            //추방자 개인에게만 알림 전송.
            publisher.publishEvent(new TopicNotifyEvent("그룹원 추방","group-2",body,user.getAlarm().getDevice_token()));
        }
    }

    /**
     * 그룹 탈퇴시 그룹원들에게 전송시키는 이벤트.
     * @param group
     * @param role
     */
    public void groupQuit(Groups group, String role) {
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("role", role);
            publisher.publishEvent(new TopicNotifyAllEvent("그룹원 탈퇴", "group-3",body,group.getTopic_name() ));
        }
    }

    /**
     * 리더 변경시 그룹원들에게 전송시키는 이벤트.
     * @param group
     * @param role
     */
    public void groupLeaderChange(Groups group, String role) {
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("role", role);
            publisher.publishEvent(new TopicNotifyAllEvent("그룹장 이전", "group-4",body,group.getTopic_name() ));
        }
    }

    /**
     * 그룹 정보 수정 시 그룹원들에게 알람 전송
     * @param group
     */
    public void groupInfoUpdate(Groups group) {
        if (group.getTopic_name() != null) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "가족 그룹 정보가 수정되었습니다.");
            publisher.publishEvent(new TopicNotifyAllEvent("그룹 정보 수정", "group-5", body, group.getTopic_name()));
        }
    }
    /**
     * 질문 작성 완료시 그룹원들에게 발생시키는 이벤트.
     * @param group
     */
    public void questionWrite(Groups group) {
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("message","그룹원이 질문에 응답했습니다. 확인해주세요" );
            publisher.publishEvent(new TopicNotifyAllEvent("그룹 질문 작성", "diary-1",body,group.getTopic_name() ));
        }
    }

    /**
     * 사진 추가시 그룹원들에게 알람을 전송하는 이벤트 메소드
     * @param group
     */
    public void photoAdd(Groups group){
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("message","앨범에 사진이 추가되었습니다. 앨범을 확인해주세요." );
            publisher.publishEvent(new TopicNotifyAllEvent("사진 추가", "album-1",body,group.getTopic_name() ));
        }
    }


    /**
     * 가족 일정 추가 시 그룹원들에게 알람 전송
     * @param group
     */
    public void familyScheduleAdd(Groups group) {
        if (group.getTopic_name() != null) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "가족 일정이 추가되었습니다. 일정을 확인하세요!");
            publisher.publishEvent(new TopicNotifyAllEvent("가족 일정 추가", "calendar-1", body, group.getTopic_name()));
        }
    }

    /**
     * 가족 일정 수정 시 (참여자 수정 포함) 그룹원들에게 알람 전송
     * @param group
     * @param scheduleId
     */
    public void familyScheduleUpdate(Groups group, Long scheduleId) {
        if (group.getTopic_name() != null) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "가족 일정 정보가 수정되었습니다. 일정을 확인하세요!");
            body.put("sch_id", String.valueOf(scheduleId));
            publisher.publishEvent(new TopicNotifyAllEvent("가족 일정 수정", "calendar-2", body, group.getTopic_name()));
        }
    }

    /**
     * 일정 코멘트 작성 시 참여자들에게 알람 전송
     * @param group
     * @param scheduleTitle
     * @param commentId
     */
    public void scheduleCommentAdd(Groups group, String scheduleTitle, Long commentId) {
        if (group.getTopic_name() != null) {
            Map<String, String> body = new HashMap<>();
            body.put("message", scheduleTitle + "에 댓글이 작성되었습니다.");
            body.put("com_id", String.valueOf(commentId));
            publisher.publishEvent(new TopicNotifyAllEvent("일정 코멘트 작성", "calendar-3", body, group.getTopic_name()));
        }
    }



}
