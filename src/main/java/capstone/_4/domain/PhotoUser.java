package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "photo_user")
public class PhotoUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="photouser_id")
    private Long id;

    @Column
    private Boolean main;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="photo_id")
    @JsonManagedReference
    private Photo photo;
}
