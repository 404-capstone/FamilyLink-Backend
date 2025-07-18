package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@ToString(exclude="groupsuser")
@Getter
public class User {

    public User() {

    }

    public User(String social, String email, String username){
        this.social=social;
        this.email=email;
        this.username=username;
    }

    @Id
    @Column(name="u_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String social;

    @Column
    private String email;

    @Column
    private String username;

    @Column
    private Integer age;

    @Column
    private String gender;

    @Column
    private String image;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonBackReference
    private List<GroupsUser> groupsuser = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PhotoUser> photouser = new ArrayList<>();

    public void addGroupUser(GroupsUser groupsuser){
        this.groupsuser.add(groupsuser);
    }


    public void addPhotoUser(PhotoUser photoUser) {
        this.photouser.add(photoUser);
    }
}
