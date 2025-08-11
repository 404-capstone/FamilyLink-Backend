package capstone._4.domain;

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

    @ManyToOne
    @JoinColumn(name = "u_id")
    @JsonManagedReference
    private User user;
}
