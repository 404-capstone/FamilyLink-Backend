package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sch_id")
    private Integer id;

    @Column(name="sch_title")
    private String title;

    @Column(name="start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "sch_content")
    private String content;

    @Column
    private Boolean permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="u_id" )
    @JsonManagedReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cal_id")
    @JsonManagedReference
    private Calendar calendar;

}
