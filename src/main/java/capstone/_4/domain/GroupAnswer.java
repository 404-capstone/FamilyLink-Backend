package capstone._4.domain;

import capstone._4.domain.question.GroupQuestion;
import capstone._4.domain.question.QuestionInventory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "group_answer")
@Getter
public class GroupAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Integer id;

    @Column(name = "gup_title")
    private String title;

    @Column(name = "user_answer")
    private String answer;

    @Column
    private boolean flag;

    @ManyToOne
    @JoinColumn(name = "u_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "gq_id")
    @JsonBackReference
    private GroupQuestion groupQuestion;

    @ManyToOne
    @JoinColumn(name = "qi_id")
    @JsonBackReference
    private QuestionInventory questionInventory;

    public void changeQuestion(GroupQuestion groupQuestion) {
        this.groupQuestion = groupQuestion;
    }

    public void insertInfo(String answer,GroupQuestion groupQuestion,User user,QuestionInventory questionInventory) {
        this.answer = answer;
        this.groupQuestion = groupQuestion;
        this.user = user;
        this.questionInventory = questionInventory;
    }
}
