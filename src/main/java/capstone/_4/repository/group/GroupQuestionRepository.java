package capstone._4.repository.group;

import capstone._4.domain.question.GroupQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupQuestionRepository extends JpaRepository<GroupQuestion,Integer> {
}
