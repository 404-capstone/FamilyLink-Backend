package capstone._4.service.other;

import capstone._4.domain.Alarm;
import capstone._4.domain.Groups;
import capstone._4.domain.GroupsUser;
import capstone._4.domain.User;
import capstone._4.exception.TokenException;
import capstone._4.repository.AlarmRepository;
import capstone._4.repository.group.GroupRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final FcmService fcmService;

    @Transactional
    public void tokenSave(String androidToken,Integer userId) {
        log.info("token:{}",androidToken);
        if(androidToken!=null) {
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

    @Transactional
    public void chageState(int userId,boolean flag) {
       Alarm alarm= alarmRepository.findByUserId(userId)
               .orElseThrow(()-> new EntityNotFoundException("알람 세팅을 찾을수 없습니다."));

       alarm.chageState(flag);

       User user=alarm.getUser();;
       Groups groups=userRepository.findGroupById(userId).get();
       if(flag) createAndAccessTopic(groups,user); //플래그가 true 면. 토픽에 참여하기.
       else quitTopic(user,groups);
    }

    @Transactional
    public void createAndAccessTopic(Groups groups,User user) {
        if(user.getAlarm()!=null &&user.getAlarm().isEnabled()) {
            log.info("fcm 토큰 존재. 토픽생성.");
            String topicname=groups.getGroup_name();
            if(topicname==null||topicname.isBlank()){
                topicname = "group" + groups.getGup_id();
                groups.changeTopic(topicname);
                log.info("토픽 생성:{}",topicname);
            }
                Alarm alarm = user.getAlarm();
                fcmService.createTopicOne(topicname, alarm.getDevice_token());
                log.info("정상 저장 완료.");
        }

    }

    public void quitTopic(User user,Groups groups) {
        if(user.getAlarm()!=null && user.getAlarm().isEnabled()) {
            log.info("토픽에서 제거.");
            String token = user.getAlarm().getDevice_token();
            fcmService.deleteOneTopic(groups.getGup_id(), token);
        }

    }

    @Transactional
    public void deleteTopic(Integer groupId) {
        Groups groups=groupRepository.findById(groupId).orElseThrow(()->
                new EntityNotFoundException("그룹이 존재하지 않습니다"));
        List<GroupsUser>groupsUsers= groups.getGroupsuser();
        List<String> tokens=groupsUsers.stream().map(GroupsUser::getUser)
                .map(User::getAlarm).filter(Objects::nonNull)
                .map(Alarm::getDevice_token).filter(Objects::nonNull).toList(); //null인거 치워버리기.
        if(!tokens.isEmpty()){
            log.info("토픽 삭제.");
            fcmService.deleteTopic(groups.getGup_id(), tokens);
        }

    }

    public void GroupAccess(Groups group,User user) {
        if(group.getTopic_name()!=null){
            fcmService.sendNotification("유저 그룹 가입",user.getUsername()+"유저가 그룹을 가입하였습니다.","groupAccess", group.getTopic_name());
        }else {
            throw new RuntimeException("토픽이 존재하지 않아 알람을 전송하지 못했습니다.");
        }
    }
}
