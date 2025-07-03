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

    @EmbeddedId
    private UserId id;

    @Column
    private String role;

    @Column
    private int score;

    @Column(columnDefinition = "tinyint(1)")
    private boolean leader;

    @ManyToOne
    @MapsId("gup_id")
    @JoinColumn(name="gup_id")
    private Gruops group;

    @OneToOne
    @MapsId("u_id")
    @JoinColumn(name = "u_id")
    private User user;
}
