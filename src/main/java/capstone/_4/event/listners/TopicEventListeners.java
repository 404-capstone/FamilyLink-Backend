package capstone._4.event.listners;

import capstone._4.event.TopicDeleteEvent;
import capstone._4.event.TopicNotifyEvent;
import capstone._4.event.TopicSubscribeEvent;
import capstone._4.event.TopicUnSubscribeEvent;
import capstone._4.service.other.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopicEventListeners {
    private final FcmService fcmService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createAndAccessTopic(TopicSubscribeEvent event) {
        log.info("fcm 토큰 존재. 토픽생성.");
        fcmService.createTopicOne(event.topicName(), event.token());
        log.info("정상 저장 완료.");

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void quitTopic(TopicUnSubscribeEvent event) {
        log.info("토픽에서 제거.");
        fcmService.deleteOneTopic(event.topicName(), event.token());
    }

    //새로운 트랜잭션에서 조회.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteTopic(TopicDeleteEvent event) {
        fcmService.deleteTopic(event.topicName(), event.tokens());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotify(TopicNotifyEvent event) {
        log.info("그룹 가입 메시지 전송.");
        fcmService.sendNotification(event.title(), event.body(), event.type(), event.topicName());
        //fcmService.sendNotification("유저 그룹 가입", user.getUsername() + "유저가 그룹을 가입하였습니다.", "groupAccess", group.getTopic_name());
    }

}


