package capstone._4.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
public class User {

    public User() {

    }

    public User(String social, String email, String username, String image) {
        this.social = social;
        this.email = email;
        this.username = username;
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String social;

    @Column
    private String email;

    @Column
    private String username;

    @Column
    private int age;

    @Column
    private String gender;

    @Column
    private String image;

    @OneToOne(mappedBy = "user")
    private Groupsuser groupsuser;



}
