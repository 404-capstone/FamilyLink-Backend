package capstone._4.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary")
@NoArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "di_id")
    private Integer id;

    @Column(name = "di_title")
    private String title;

    @Column(name = "di_content")
    private String content;

    @Column(name = "diary_at")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "u_id")
    private User user;
}
