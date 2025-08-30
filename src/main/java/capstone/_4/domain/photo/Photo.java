package capstone._4.domain.photo;

import capstone._4.domain.Album;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    public Photo(String title,LocalDate date,LocalTime time, String area, String content) {
        this.title=title;
        this.date=date;
        this.time=time;
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
    private LocalDate date;

    @Column(name="photo_time")
    private LocalTime time;

    @Column(name="photo_area")
    private String area;

    @Column(name="photo_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonBackReference
    private Album album;

    @OneToMany(mappedBy = "photo",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PhotoUser> photoUser=new ArrayList<>();

    @OneToMany(mappedBy = "photo",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @JsonManagedReference
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

    public void editInfo(String title,LocalDate date,LocalTime time,String area,String content){
        if(title!=null) this.title=title;
        if(date!=null) this.date=date;
        if(time!=null) this.time=time;
        if(area!=null) this.area=area;
        if(content!=null) this.content=content;

    }

    public void changeAlbum(Album newAlbum){
        if(this.album != null){ this.album.getPhoto().remove(this);}
        this.album=newAlbum;
        if(newAlbum!=null) newAlbum.changeAlbum(this);
    }

    public void removeUser(PhotoUser photoUser) {
        this.photoUser.remove(photoUser);
    }
}
