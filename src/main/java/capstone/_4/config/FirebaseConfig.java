package capstone._4.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {
    @Value("${firebase.service-account.path}")
    private String SERVICE_ACCOUNT_PATH;

    @Bean
    public FirebaseApp firebaseApp(){
        try{
            FirebaseOptions options=FirebaseOptions.builder()
                    .setCredentials( //파이어베이스 비공개키 이용해서 인증및 접속.
                            GoogleCredentials.fromStream(new ClassPathResource(SERVICE_ACCOUNT_PATH).getInputStream())
                    )
                    .build();
            log.info("succes init firebase app");
            return FirebaseApp.initializeApp(options); //파이어베이스 첫초기화
        }catch (IOException e){
            log.error("파이어 베이스 연결에 실패 :{}",e.getMessage());
            throw new RuntimeException("파이어베이스 연결 실패:"+e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp){
        return FirebaseMessaging.getInstance(firebaseApp); //전송을 위한 message인스턴스 생성.
    }
}
