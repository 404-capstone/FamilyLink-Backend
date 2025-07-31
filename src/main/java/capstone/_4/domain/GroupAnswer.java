package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "group_answer")
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
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "gq_id")
    @JsonManagedReference
    private GroupQuestion groupQuestion;
}
