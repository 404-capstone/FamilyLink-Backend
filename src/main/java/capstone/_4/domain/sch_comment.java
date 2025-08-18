package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sch_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class sch_comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "com_id")
    private Long id;

    @Column(name = "sch_body", length = 500)
    private String body;

    @Column(name = "date_at")
    private LocalDate dateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sch_id", nullable = false)
    @JsonBackReference
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
