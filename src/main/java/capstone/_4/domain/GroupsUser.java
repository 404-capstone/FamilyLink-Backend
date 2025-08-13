package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Integer groupUserId;


    @Column
    private String role;

    @Column
    private Integer score;

    @Column(name="correct_percent")
    private Integer percent;

    @Column
    private String level;

    @Column(columnDefinition = "tinyint(1)")
    private Boolean leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gup_id")
    @JsonBackReference
    private Groups group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    @JsonBackReference
    private User user;

    public void setScore(int score,int percent,String level) {
        this.score=score;
        this.percent=percent;
        this.level=level;
    }

    public void changeLeader(boolean b) {
        this.leader=b;
    }
}
