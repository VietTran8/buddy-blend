package vn.edu.tdtu.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.models.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse implements Serializable {
    private String id;
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;
    private EPrivacy privacy;
    private EPostType type;
    private User user;
    private int noShared;
    private long noComments;
    private int noReactions;
    private List<TopReacts> topReacts;
    private EReactionType reacted;
    private ShareInfo shareInfo;
    private GroupInfo groupInfo;
    private List<User> taggedUsers;
    private boolean isMine;
    @JsonIgnore
    private Date hiddenCreatedAt;
}
