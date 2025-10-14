package capstone._4.event;

import java.util.Map;

public record TopicUnSubscribeSendEvent(String title, String type, Map<String,String> body, String topic,String token) {

}
