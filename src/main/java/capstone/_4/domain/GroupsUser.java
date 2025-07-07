package capstone._4.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`groups_user`")
@NoArgsConstructor
@Getter
@Setter
public class GroupsUser {

    public GroupsUser(Groups group, User user,String role, boolean leader) {
        this.group=group;
        this.user=user;
        this.leader=leader;
        this.role=role;

        this.group.addUser(this);
        this.user.addGroupUser(this);
    }

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
