package vn.edu.tdtu.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.dto.response.MinimizedUserResponse;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.enums.EReactionType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponse implements Serializable {
    private String id;
    private String content;
//    private List<String> imageUrls;
//    private List<String> videoUrls;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;
    private EPrivacy privacy;
    private EPostType type;
    private MinimizedUserResponse user;
    private int noShared;
    private int noComments;
    private int noReactions;
    private List<TopReacts> topReacts;
    private EReactionType reacted;
    private List<Media> medias;
    private PostResponse sharedPost;
    private GroupInfo groupInfo;
    private String background;
    private List<MinimizedUserResponse> taggedUsers;
    private boolean isMine;
    private boolean saved;
    private boolean illegal;
}
