package capstone._4.event;

import java.util.Map;

public record TopicNotifyEvent (String title,String type,Map<String,String> body, String token){}
