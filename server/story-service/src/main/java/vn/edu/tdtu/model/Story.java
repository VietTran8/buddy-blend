package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EMediaType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.enums.EStoryFont;
import vn.edu.tdtu.enums.EStoryType;

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
    private String thumbnailUrl;
    private String content;
    @Enumerated(EnumType.STRING)
    private EStoryFont font;
    @Enumerated(EnumType.STRING)
    private EMediaType mediaType;
    @Enumerated(EnumType.STRING)
    private EStoryType storyType;
    private String background;
    @JsonIgnore
    private String userId;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonIgnore
    private LocalDateTime expiredAt;
    @Enumerated(EnumType.STRING)
    private EPrivacy privacy;
    @JsonIgnore
    @OneToMany(mappedBy = "story", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private List<Viewer> viewers;
}
