package capstone._4.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class UserId {

    @Column
    private int u_id;
    @Column
    private int gup_id;
}
