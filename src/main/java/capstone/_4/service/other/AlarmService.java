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

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final UserRepository userRepository;
    private final GroupsUserRepository groupsUserRepository;
    private final GroupRepository groupRepository;

    private final AlarmRepository alarmRepository;
    private final ApplicationEventPublisher publisher;

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

    public void quitTopic(User user, Groups groups) {
        if (user.getAlarm() != null && user.getAlarm().isEnabled()) {
            log.info("토픽에서 제거.");
            String token = user.getAlarm().getDevice_token();
            publisher.publishEvent(new TopicUnSubscribeEvent(groups.getTopic_name(), token));
        }

    }

    public void groupAccess(Groups group, String role) {
        if(group.getTopic_name()!=null) {
            log.info("그룹 가입 메시지 전송.");
            Map<String,String> body = new HashMap<>();
            body.put("role",role);
            publisher.publishEvent(new TopicNotifyAllEvent("그룹 가입","group-1",body,group.getTopic_name()));
        }
    }

    public void groupUserDelete(User user){
        if(user.getAlarm()!=null&&user.getAlarm().isEnabled()) {
            Map<String,String> body = new HashMap<>();
            body.put("message","가족 그룹에서 추방되었습니다.");
            publisher.publishEvent(new TopicNotifyEvent("그룹원 퇴장","group-2",body,user.getAlarm().getDevice_token()));
        }
    }

    public void groupQuit(Groups group, String role) {
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("role", role);
            publisher.publishEvent(new TopicNotifyAllEvent("그룹원 탈퇴", "group-3",body,group.getTopic_name() ));
        }
    }

    public void groupLeaderChange(Groups group, String role) {
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("role", role);
            publisher.publishEvent(new TopicNotifyAllEvent("그룹장 이전", "group-4",body,group.getTopic_name() ));
        }
    }

    public void questionWrite(Groups group) {
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("message","그룹원이 질문에 응답했습니다. 확인해주세요" );
            publisher.publishEvent(new TopicNotifyAllEvent("그룹 질문 작성", "diary-1",body,group.getTopic_name() ));
        }
    }

    public void photoAdd(Groups group){
        if(group.getTopic_name()!=null) {
            Map<String, String> body = new HashMap<>();
            body.put("message","앨범에 사진이 추가되었습니다. 앨범을 확인해주세요." );
            publisher.publishEvent(new TopicNotifyAllEvent("사진 추가", "album-1",body,group.getTopic_name() ));
        }
    }




}
