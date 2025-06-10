package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.tdtu.common.enums.post.EPostType;
import vn.tdtu.common.enums.post.EPrivacy;

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
    private List<String> mediaIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EPrivacy privacy;
    private Boolean active;
    @Indexed
    private String groupId;
    private String background;
    private String sharedPostId;
    @Indexed
    private EPostType type;
    @Indexed
    private String userId;
    @Indexed
    private boolean detached;
    private List<PostTag> postTags;
    private String normalizedContent;
}