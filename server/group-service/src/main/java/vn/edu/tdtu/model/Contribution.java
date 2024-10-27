package vn.edu.tdtu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String postId;
    @ManyToOne
    @JoinColumn(name = "groupId")
    private Group group;
}
