package capstone._4.domain.question;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_info_list")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionInfoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qil_id")
    private Integer id;

    @Column
    private Integer slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qulist_id")
    @JsonManagedReference
    private QuestionList questionList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qi_id")
    @JsonManagedReference
    private QuestionInventory questionInventory;

    public void changeList(QuestionList questionList) {
        this.questionList=questionList;
    }

    public void changeInventory(QuestionInventory questionInventory) {
        this.questionInventory=questionInventory;
    }

    public void changeSlot(int i) {
        this.slot = i;
    }
}
