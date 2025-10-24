package capstone._4.event;

import java.util.List;
import java.util.Map;

public record TopicNotifySelectEvent(String title, String type, Map<String,String> body, List<String> token) {
}
