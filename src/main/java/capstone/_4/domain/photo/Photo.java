package capstone._4.domain.photo;

import capstone._4.domain.Album;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    public Photo(LocalDateTime date,String area, String content) {
        this.date=date;
        this.area=area;
        this.content=content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="photo_id")
    private Integer id;

    @Column(name="photo_title")
    private String title;

    @Column(name="photo_date")
    private LocalDateTime date;

    @Column(name="photo_area")
    private String area;

    @Column(name="photo_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonManagedReference
    private Album album;

    @OneToMany(mappedBy = "photo",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PhotoUser> photoUser=new ArrayList<>();

    @OneToMany(mappedBy = "photo",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PhotoImage> photoImages=new ArrayList<>();

    public void addPhotourl(PhotoImage photoImage){
        photoImages.add(photoImage);
        photoImage.setPhoto(this);
    }

    public void setPhotoImages(List<PhotoImage> photoImages) {
        this.photoImages=photoImages;
    }

    public void setAlbum(Album album) {
        this.album=album;
    }

    public void addPhotoUser(PhotoUser photoUser){
        this.photoUser.add(photoUser);
    }

    public void editInfo(String title,LocalDateTime date,String area,String content){
        this.title=title;
        this.date=date;
        this.area=area;
        this.content=content;
    }

    public void removeUser(PhotoUser photoUser) {
        this.photoUser.remove(photoUser);
    }
}
