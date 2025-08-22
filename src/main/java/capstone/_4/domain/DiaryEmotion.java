package capstone._4.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary_emotion")
@Getter
@NoArgsConstructor
public class DiaryEmotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer de_id;

    private String emotion;
    private Double score;

    @ManyToOne
    @JoinColumn(name="di_id")
    private Diary diary;

    public void changeEmotion(String emotion, Double score,Diary diary) {
        this.emotion = emotion;
        this.score = score;
        this.diary = diary;
        diary.addEmotions(this);
    }
}
