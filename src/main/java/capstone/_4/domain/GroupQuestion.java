package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "group_question")
@Getter
@NoArgsConstructor
public class GroupQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gq_id")
    private Integer id;

    @Column
    private LocalDate day;

    @Column
    private boolean slot;

    @ManyToOne
    @JoinColumn(name = "gup_id")
    @JsonManagedReference
    private Groups groups;

    @ManyToOne
    @JoinColumn(name="qu_id")
    @JsonManagedReference
    private QuestionInventory questionInventory;

    @ManyToOne
    @JoinColumn(name="qulist_id")
    @JsonManagedReference
    private QuestionList questionList;

    @OneToMany(mappedBy = "groupQuestion",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<GroupAnswer>  groupAnswers;


}
