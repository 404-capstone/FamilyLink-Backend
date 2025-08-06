package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@NoArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cal_id")
    private Integer id;

    @Column(name="cal_title")
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gup_id")
    @JsonManagedReference
    private Groups groups;

    @OneToMany(mappedBy = "calendar",fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Schedule> schedule = new ArrayList<>();
}
