package capstone._4.domain;

import capstone._4.domain.photo.Photo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Album {

    public Album(Integer year,Integer month,Groups groups){
        this.year=year;
        this.month=month;
        this.groups=groups;
    }

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

    public void addPhoto(Photo photo){
        this.photo.add(photo);
    }
}
