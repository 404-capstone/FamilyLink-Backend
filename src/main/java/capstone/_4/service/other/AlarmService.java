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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final FcmService fcmService;

    public void tokenSave(String androidToken,Integer userId) {
        Alarm alarm=alarmRepository.findByUserId(userId)
                .get();
        if(alarm==null){
            log.info("알람 새로 생성");
            User user=userRepository.findById(userId).get();
            alarm=new Alarm(androidToken,user);
        }else{
            log.info("알람 토큰 변경.");
            alarm.changeToken(androidToken);
        }

    }

    public void chageState(int userId,boolean flag) {
       Alarm alarm= alarmRepository.findByUserId(userId)
               .orElseThrow(()-> new EntityNotFoundException("알람 세팅을 찾을수 없습니다."));

       alarm.chageState(flag);
       User user=userRepository.findById(userId).get();
       Groups groups=userRepository.findGroupById(userId).get();
       if(flag) createAndAccessTopic(groups,user); //플래그가 true 면. 토픽에 참여하기.
       else quitTopic(user,groups);
    }

    public void createAndAccessTopic(Groups groups,User user) {
        if(user.getAlarm().isEnabled()) {
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
        if(user.getAlarm().isEnabled()) {
            log.info("토픽에서 제거.");
            String token = user.getAlarm().getDevice_token();
            fcmService.deleteOneTopic(groups.getGup_id(), token);
        }

    }

    public void deleteTopic(Groups groups) {
        List<GroupsUser>groupsUsers= groups.getGroupsuser();
        List<String> tokens=groupsUsers.stream().map(groupsUser -> {
            return groupsUser.getUser().getAlarm().getDevice_token();
        }).toList();
        if(tokens.size()>0){
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
