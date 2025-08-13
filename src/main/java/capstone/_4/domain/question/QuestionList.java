package capstone._4.domain.question;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "question_list")
public class QuestionList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qulist_id")
    private Integer id;

    @Column
    private String title;

    @Column
    private LocalDateTime created;

    @OneToMany(mappedBy = "questionList",fetch = FetchType.LAZY,
    cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<QuestionInfoList> questionInfoList=new ArrayList<>();

    @OneToMany(mappedBy = "",fetch = FetchType.LAZY,
    cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<GroupQuestion> groupQuestionList=new ArrayList<>();


    public void changeInfo(QuestionInfoList questionInfoList) {
        this.questionInfoList.add(questionInfoList);
        questionInfoList.changeList(this);
    }

    public void addQuestion(GroupQuestion groupQuestion) {
        this.groupQuestionList.add(groupQuestion);
    }
}
