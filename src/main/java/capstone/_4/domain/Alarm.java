package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarm")
@Getter
@NoArgsConstructor
public class Alarm {

    public Alarm(User user) {
        this.user=user;
        user.chageAlarm(this);
    }

    public Alarm(String androidToken,User user) {
        this.device_token=androidToken;
        this.user=user;
        user.chageAlarm(this);
    }

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    @JsonManagedReference
    private User user;


    public void changeToken(String androidToken) {
        this.device_token=androidToken;
    }

    public void chageState(boolean flag) {
        this.enabled=flag;
    }
}
