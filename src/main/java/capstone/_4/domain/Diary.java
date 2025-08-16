package capstone._4.domain;

import capstone._4.domain.question.GroupQuestion;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "di_id")
    private Integer id;

    @Column(name = "di_content")
    private String content;

    @Column(name = "diary_at")
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gq_id")
    @JsonBackReference
    private GroupQuestion groupQuestion;

    @ManyToOne
    @JoinColumn(name = "u_id")
    @JsonBackReference
    private User user;
}
