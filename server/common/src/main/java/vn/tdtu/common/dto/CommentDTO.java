package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String id;
    private String content;
    private String parentId;
    private List<String> imageUrls;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;
    private UserDTO user;
    private List<ReactionDTO> topReacts;
    private int noReactions;
    private EReactionType reacted;
    private boolean isMine;
    private List<CommentDTO> children;
}
