package vn.edu.tdtu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Blocking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDateTime blockedAt;
    @ManyToOne
    @JoinColumn(name = "blockedById")
    private User blockedByUser;
    @ManyToOne
    @JoinColumn(name = "blockedId")
    private User blockedUser;
}
