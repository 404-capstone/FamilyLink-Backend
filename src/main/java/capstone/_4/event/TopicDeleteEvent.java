package capstone._4.event;

import java.util.List;

public record TopicDeleteEvent(String topicName, List<String> tokens) {
}
