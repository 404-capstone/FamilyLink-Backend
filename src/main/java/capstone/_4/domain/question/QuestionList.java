package capstone._4.domain.question;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @JsonBackReference
    private List<QuestionInfoList> questionInfoList=new ArrayList<>();

    public void changeInfo(QuestionInfoList questionInfoList) {
        this.questionInfoList.add(questionInfoList);
        questionInfoList.changeList(this);
    }

}
