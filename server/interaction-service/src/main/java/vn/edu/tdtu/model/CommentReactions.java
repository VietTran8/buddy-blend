package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.time.LocalDateTime;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentReactions {
    @Id
    private String id;
    private EReactionType type;
    private LocalDateTime createdAt;
    private String userId;
    private String cmtId;
}
