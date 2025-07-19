package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo_user")
@Getter
@NoArgsConstructor
public class PhotoUser {

    public PhotoUser(User user,Photo photo) {
        this.user=user;
        this.photo=photo;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="photouser_id")
    private Integer id;

    @Column
    private Boolean main;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    @JsonManagedReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="photo_id")
    @JsonManagedReference
    private Photo photo;
}
