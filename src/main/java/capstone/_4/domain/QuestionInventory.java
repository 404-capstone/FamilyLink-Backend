package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JsonBackReference
    private List<QuestionInfoList> questionInfoList;

}
