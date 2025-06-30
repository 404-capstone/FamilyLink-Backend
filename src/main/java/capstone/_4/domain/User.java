package capstone._4.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @Column(name="u_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String social;

    @Column
    private String email;

    @Column
    private int age;

    @Column
    private String gender;

    @Column
    private String image;

    @OneToOne(mappedBy = "user")
    private Groupsuser groupsuser;

}
