package capstone._4.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동 생성
@Table(name = "diary_emotion")
public class DiaryEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "de_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String emotion;

    @Column(nullable = false)
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "di_id", nullable = false)
    private Diary diary;

    // 필요한 생성자만 추가 (lombok의 @AllArgsConstructor 사용 가능)
    public DiaryEmotion(String emotion, Double score, Diary diary) {
        this.emotion = emotion;
        this.score = score;
        this.diary = diary;
    }
}
