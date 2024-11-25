package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EReactionType;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private EReactionType type;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonIgnore
    private String userId;
    @ManyToOne
    @JoinColumn(name = "viewerId")
    @JsonIgnore
    private Viewer viewer;
}
