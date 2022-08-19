package photsup.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "USR")
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"username", "avatarUrl"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String uniqueKey;
    private String avatarUrl;
    private String status;
}
