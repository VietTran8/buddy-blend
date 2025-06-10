package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.enums.post.EPostType;
import vn.tdtu.common.enums.post.EPrivacy;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO implements Serializable {
    @Schema(description = "Post ID")
    private String id;

    @Schema(description = "Post content")
    private String content;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "The date and time when the post was created, formatted as dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "The date and time when the post was last updated, formatted as dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;

    @Schema(description = "The privacy setting of the post")
    private EPrivacy privacy;

    @Schema(description = "The type of the post")
    private EPostType type;

    @Schema(description = "The user who created the post")
    private UserDTO user;

    @Schema(description = "List of media associated with the post")
    private List<MediaDTO> medias;

    @Schema(description = "Number of times the post has been shared")
    private int noShared;

    @Schema(description = "Number of comments on the post")
    private long noComments;

    @Schema(description = "Number of reactions to the post")
    private int noReactions;

    @Schema(description = "List of top reactions to the post")
    private List<ReactionDTO> topReacts;

    @Schema(description = "The reaction type of the current user")
    private EReactionType reacted;

    @Schema(description = "The post that was shared, if applicable")
    private PostDTO sharedPost;

    @Schema(description = "Background information or content of the post")
    private String background;

    @Schema(description = "Group information associated with the post")
    private GroupDTO groupInfo;

    @Schema(description = "List of users tagged in the post")
    private List<UserDTO> taggedUsers;

    @Schema(description = "Indicates if the post belongs to the current user")
    private boolean isMine;

    @Schema(description = "Indicates if the post is saved by the current user")
    private boolean saved;

    @Schema(description = "Indicates if the post is marked as illegal")
    private boolean illegal;

    @JsonIgnore
    @Schema(description = "The hidden creation date of the post, ignored in serialization")
    private Date hiddenCreatedAt;
}
