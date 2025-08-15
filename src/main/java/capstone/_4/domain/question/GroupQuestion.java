package capstone._4.domain.question;

import capstone._4.domain.GroupAnswer;
import capstone._4.domain.Groups;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_question")
@Getter
@NoArgsConstructor
public class GroupQuestion {

    public GroupQuestion(LocalDate date) {
        this.day=date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gq_id")
    private Integer id;

    @Column
    private LocalDate day;

    @ManyToOne
    @JoinColumn(name = "gup_id")
    @JsonBackReference
    private Groups groups;

    @ManyToOne
    @JoinColumn(name="qulist_id")
    @JsonBackReference
    private QuestionList questionList;


    @OneToMany(mappedBy = "groupQuestion",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<GroupAnswer>  groupAnswers=new ArrayList<>();

    public void changeGroupAnswer(GroupAnswer groupAnswer){
        this.groupAnswers.add(groupAnswer);
        groupAnswer.changeQuestion(this);
    }


    public void changeQuestion(QuestionList questionList,Groups groups) {
        this.questionList=questionList;
        questionList.addQuestion(this);
        this.groups=groups;
        groups.addQuestion(this);
    }
}
