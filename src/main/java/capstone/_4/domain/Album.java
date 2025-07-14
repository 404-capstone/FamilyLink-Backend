package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@AllArgsConstructor
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="album_id")
    private Integer id;

    @Column(name = "album_name")
    private String name;

    @Column
    private Integer year;

    @Column
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gup_id")
    @JsonManagedReference
    private Groups groups;

    @OneToMany(mappedBy = "album",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Photo> photo=new ArrayList<>();
}
