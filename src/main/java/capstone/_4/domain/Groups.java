package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`groups`")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Groups {

    public Groups(String name) {
        this.group_name=name;
    }

    public Groups(String name, String image) {
        this.group_name=name;
        this.image=image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gup_id;

    @Column(name = "groupname")
    private String group_name;

    @Column
    private String code;

    @Column
    private String image;

    @OneToMany(mappedBy = "group",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<GroupsUser> groupsuser = new ArrayList<>();

    public void addUser(GroupsUser groupsuser){
        this.groupsuser.add(groupsuser);
    }
    public void setCode(String code){this.code=code;}

}
