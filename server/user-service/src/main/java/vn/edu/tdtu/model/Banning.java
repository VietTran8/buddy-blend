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
public class Banning {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDateTime bannedAt;
    @ManyToOne
    @JoinColumn(name = "bannedById")
    private User bannedByUser;
    @ManyToOne
    @JoinColumn(name = "bannedId")
    private User bannedUser;
}
