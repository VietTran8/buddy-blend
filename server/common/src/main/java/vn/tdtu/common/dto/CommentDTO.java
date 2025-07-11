package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO implements Serializable {

    @Schema(description = "Unique identifier of the comment")
    private String id;

    @Schema(description = "Content of the comment")
    private String content;

    @Schema(description = "Identifier of the parent comment, if this is a reply")
    private String parentId;

    @Schema(description = "List of URLs for images attached to the comment")
    private List<String> imageUrls;

    @Schema(description = "Date and time when the comment was created, formatted as dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    @Schema(description = "Date and time when the comment was last updated, formatted as dd/MM/yyyy HH:mm:ss")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;

    @Schema(description = "Details of the user who created the comment")
    private UserDTO user;

    @Schema(description = "List of top reactions to the comment")
    private List<ReactionDTO> topReacts;

    @Schema(description = "Total number of reactions to the comment")
    private int noReactions;

    @Schema(description = "Reaction type of the current user to the comment")
    private EReactionType reacted;

    @Schema(description = "Indicates whether the comment belongs to the current user")
    private boolean isMine;

    @Schema(description = "List of child comments, if this is a parent comment")
    private List<CommentDTO> children;

}
