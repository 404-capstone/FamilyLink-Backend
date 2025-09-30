package capstone._4.event;

import java.util.Map;

//개인에게만 전송하는 알림 이벤트 포맷.
public record TopicNotifyEvent (String title,String type,Map<String,String> body, String token){}
