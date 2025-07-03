package capstone._4.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Gruops {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gup_id;

    @Column(name = "groupname")
    private String group_name;

    @Column
    private String code;

    @Column
    private String image;

    @OneToMany(mappedBy = "group")
    private List<Groupsuser> groupsuser;

}
