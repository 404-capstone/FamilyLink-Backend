package capstone._4.service.other;

import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String title,String message,String type,String status,String token){
        log.info("전송 시작(title:{},message:{},type:{},status:{},token:{})",title,message,type,status,token);
        send(createMessage(title,message,type,status,token));
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

    private Message createMessage(String title, String message, String type, String status, String token) {
        return Message.builder()
                .putData("title", title)
                .putData("message", message)
                .putData("type", type)
                .putData("status", status)
                .setToken(token)
                .build();

    }
}
