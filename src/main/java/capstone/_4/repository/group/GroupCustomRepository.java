package capstone._4.repository.group;

import capstone._4.domain.Groups;
import capstone._4.domain.QGroups;
import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionList;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public interface GroupCustomRepository {



    public Optional<Groups> findById(int groupid);


    public int deleteGroupe(Integer groupId);


    public Long updateGroup(Integer groupId,String name ,String imagePath,String imageName);


}
