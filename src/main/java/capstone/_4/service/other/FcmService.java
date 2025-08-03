package capstone._4.service.other;

import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public String createTopic(Integer groupId, List<String> tokens){
        String topicName="group"+groupId;
        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(tokens, (topicName));
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;

    }

    public String deleteTopic(Integer groupId,List<String> tokens){
        String topicName="group"+groupId;
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(tokens, (topicName));
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;
    }

    public String deleteOneTopic(Integer groupId,String tokens){
        String topicName="group"+groupId;
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(Collections.singletonList(tokens), (topicName));
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;
    }



    public void sendNotification(String title,String message,String type,String status,String topic){
        log.info("전송 시작(title:{},message:{},type:{},status:{},token:{})",title,message,type,status,topic);
        send(createMessage(title,message,type,status,topic));
    }

    private void send(Message message)  {
        try {
            String response= firebaseMessaging.send(message);
        }catch (FirebaseMessagingException e){
            log.error(e.getMessage());
            try {
                throw new FirebaseException(ErrorCode.CANCELLED, "파이어베이스 문자발생중 문제 발생:"+e.getMessage(), e);
            } catch (FirebaseException ex) {
                throw new RuntimeException(ex);
            }
        }catch (FirebaseException e){
            log.error(e.getMessage());
            throw new RuntimeException("파이어베이스에서 문제발생:"+e.getMessage());

        }
    }

    private Message createMessage(String title, String message, String type, String status, String topic) {
        return Message.builder()
                .putData("title", title)
                .putData("message", message)
                .putData("type", type)
                .putData("status", status)
                .setTopic(topic)
                .build();

    }
}
