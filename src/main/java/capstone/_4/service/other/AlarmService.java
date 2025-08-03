package capstone._4.service.other;

import capstone._4.domain.Alarm;
import capstone._4.domain.User;
import capstone._4.repository.AlarmRepository;
import capstone._4.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public void tokenSave(String androidToken,Integer userId) {
        Alarm alarm=alarmRepository.findByUserId(userId)
                .get();
        if(alarm==null){
            User user=userRepository.findById(userId).get();
            alarm=new Alarm(androidToken,user);
        }else{
            alarm.changeToken(androidToken);
        }

    }

    public void chageState(int userId,boolean flag) {
       Alarm alarm= alarmRepository.findByUserId(userId)
               .orElseThrow(()-> new EntityNotFoundException("알람 세팅을 찾을수 없습니다."));

       alarm.chageState(flag);
    }
}
