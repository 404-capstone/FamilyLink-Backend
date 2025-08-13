package capstone._4.domain.question;

import capstone._4.domain.GroupAnswer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="question_inventory")
@NoArgsConstructor
@Getter
public class QuestionInventory {

    public QuestionInventory(String content) {
        this.content = content;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qi_id")
    private Integer id;

    @Column(name = "qi_content")
    private String content;

    @Column(name= "qi_number")
    private Integer number;

    @OneToMany(mappedBy = "questionInventory",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<QuestionInfoList> questionInfoList=new ArrayList<>();

    @OneToMany(mappedBy = "questionInventory",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<GroupAnswer> groupAnswerList=new ArrayList<>();

    public void changeInfo(QuestionInfoList questionInfoList) {
        this.questionInfoList.add(questionInfoList);
        questionInfoList.changeInventory(this);
    }

}
