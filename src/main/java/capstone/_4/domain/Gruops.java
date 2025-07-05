package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
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
    @JsonIgnore
    private List<Groupsuser> groupsuser;

}
