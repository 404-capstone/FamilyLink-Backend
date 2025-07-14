package capstone._4.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="photo_id")
    private Integer id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name="photo_area")
    private String area;

    @Column(name="photo_content")
    private String content;

    @Column(name="photo_image")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonManagedReference
    private Album album;

    @OneToMany(mappedBy = "photo",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PhotoUser> photoUser=new ArrayList<>();
}
