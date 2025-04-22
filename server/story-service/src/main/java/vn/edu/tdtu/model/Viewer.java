package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Viewer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @JsonIgnore
    private String userId;
    private LocalDateTime viewedAt;
    @ManyToOne()
    @JoinColumn(name = "storyId")
    @JsonIgnore
    private Story story;
    @JsonIgnore
    @OneToMany(mappedBy = "viewer", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<Reaction> reactions;
}
