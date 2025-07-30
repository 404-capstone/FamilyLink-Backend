package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarm")
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="alarm_id")
    private Integer id;

    @Column
    private String device_token;

    @Column
    private boolean enabled;

    @Column
    private LocalDateTime last_active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    @JsonManagedReference
    private User user;



}
