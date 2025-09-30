package capstone._4.service.other;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public String createTopic(String topicName, List<String> tokens){
        //String topicName="group"+groupId;
        log.info("토픽으로 인원");
        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(tokens, (topicName));
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;

    }

    public String createTopicOne(String topicName, String tokens){
        //String topicName="group"+groupId;
        log.info("토픽 추가.");
        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(Collections.singletonList(tokens), (topicName));
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;

    }

    public String deleteTopic(String topicName,List<String> tokens){
        //String topicName="group"+groupId;
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(tokens, (topicName));
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;
    }

    public String deleteOneTopic(String topicName,String tokens){
        //String topicName="group"+groupId;
        try {
            if (topicName != null) {
                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(Collections.singletonList(tokens), (topicName));
            }
        }catch (FirebaseMessagingException e){
            throw new RuntimeException("토픽 생성중에 문제발생:"+e.getMessage());
        }
        return topicName;
    }



    public void sendNotificationAll(String title, String type, Map<String,String> body, String topic){
        log.info("전송 시작(title:{},message:{},body:{})",title,type,body);
        send(createTopicMessage(title,type,body,topic));
    }

    public void sendNotification(String title, String type, Map<String, String> body, String token) {
        log.info("개인 전송");
        send(createMessage(title,type,body,token));
    }

    private void send(Message message)  {
        try {
            String response= firebaseMessaging.send(message);
        }catch (FirebaseMessagingException e) {
            log.error("Firebase 메시지 전송 실패", e);
            throw new RuntimeException(
                    new FirebaseException(
                            ErrorCode.CANCELLED,
                            "파이어베이스 문자발생중 문제 발생: " + e.getMessage(),
                            e
                    )
            );
        }
    }

    private Message createTopicMessage(String title, String message, String type, String topic) {
        return Message.builder()
                .putData("title", title)
                .putData("message", message)
                .putData("type", type)
                .setTopic(topic)
                .build();

    }

    private Message createTopicMessage(String title, String code, Map<String,String> body, String topic) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String bodyJson=mapper.writeValueAsString(body);
            return Message.builder()
                    .putData("title",title)
                    .putData("code",code)
                    .putData("data",bodyJson)
                    .setTopic(topic)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알람 포맷 변환중 오류가 발생했습니다."+e.getMessage());
        }

    }

    private Message createMessage(String title, String code, Map<String,String> body, String token) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String bodyJson=mapper.writeValueAsString(body);
            return Message.builder()
                    .putData("title",title)
                    .putData("code",code)
                    .putData("data",bodyJson)
                    .setToken(token)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알람 포맷 변환중 오류가 발생했습니다."+e.getMessage());
        }

    }





}
