package capstone._4.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "groups_user")
@Getter
@Setter
public class Groupsuser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guser_id")
    private int groupUserId;


    @Column
    private String role;

    @Column
    private int score;

    @Column(columnDefinition = "tinyint(1)")
    private boolean leader;

    @ManyToOne
    @JoinColumn(name="gup_id")
    private Groups group;

    @ManyToOne
    @JoinColumn(name = "u_id")
    private User user;
}
