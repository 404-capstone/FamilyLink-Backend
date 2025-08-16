package capstone._4.domain.photo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="photoimage")
@Getter
@NoArgsConstructor
public class PhotoImage {

    public PhotoImage(String fileName,String fileUrl){
        this.name=fileName;
        this.url=fileUrl;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="image_id")
    private Integer id;

    @Column(name = "image_name")
    private String name;

    @Column(name = "image_url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="photo_id")
    @JsonBackReference
    private Photo photo;


    public void setPhoto(Photo photo) {
        this.photo=photo;
    }
}
