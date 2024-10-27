package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPrivacy;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String mediaUrl;
    @JsonIgnore
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    @Enumerated(EnumType.STRING)
    private EPrivacy privacy;
    @JsonIgnore
    @OneToMany(mappedBy = "story", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private List<Viewer> viewers;
}
