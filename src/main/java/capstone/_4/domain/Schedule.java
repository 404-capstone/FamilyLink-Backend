package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "schedule")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sch_id")
    private Integer id;

    @Column(name="sch_title")
    private String title;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column(name = "sch_content")
    private String content;

    @Column
    private String location;

    @Column
    private Boolean permission;

    @Column(name = "timeflex", nullable = false)
    private Boolean timeflex;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="u_id" )
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cal_id")
    @JsonBackReference
    private Calendar calendar;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GroupsSchedule> groupsSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gup_id")
    @JsonBackReference
    private Groups group;
}
