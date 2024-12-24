package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;

import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @Indexed
    private String id;
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EPrivacy privacy;
    private Boolean active;
    private String groupId;
    @Indexed
    private EPostType type;
    @Indexed
    private String userId;
    private List<PostTag> postTags;
    private String normalizedContent;
}