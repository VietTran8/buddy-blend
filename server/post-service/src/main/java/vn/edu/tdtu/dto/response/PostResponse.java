package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.data.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class PostResponse implements Serializable {
    private String id;
    private String content;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;
    private EPrivacy privacy;
    private EPostType type;
    private User user;
    private List<Media> medias;
    private int noShared;
    private long noComments;
    private int noReactions;
    private List<TopReacts> topReacts;
    private EReactionType reacted;
    private PostResponse sharedPost;
    private String background;
    private GroupInfo groupInfo;
    private List<User> taggedUsers;
    private boolean isMine;
    private boolean saved;
    private boolean illegal;
    @JsonIgnore
    private Date hiddenCreatedAt;
}
