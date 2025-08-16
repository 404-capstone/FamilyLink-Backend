package capstone._4.domain;

import capstone._4.domain.photo.PhotoUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    public User(String social, String email, String username) {
        this.social = social;
        this.email = email;
        this.username = username;
    }

    public User(String social,String email,String username,String image){
        this.social = social;
        this.email = email;
        this.username = username;
        this.image = image;
    }
    public void updateProfile(String username, Integer age, String gender) {
        this.username = username;
        this.age = age;
        this.gender = gender;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
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
    @JsonManagedReference
    private List<GroupsUser> groupsuser = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PhotoUser> photouser = new ArrayList<>();

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonManagedReference
    private Alarm alarm;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Diary> diary = new ArrayList<>();


    public void addGroupUser(GroupsUser groupsuser){
        this.groupsuser.add(groupsuser);
    }


    public void addPhotoUser(PhotoUser photoUser) {
        this.photouser.add(photoUser);
    }

    public void chageAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
