package capstone._4.event;

import java.util.Map;

// 그룹에 등록된 그룹원들에게 알람을 전송하는 포맷.
public record TopicNotifyAllEvent(String title, String type, Map<String,String> body, String topic){
}
