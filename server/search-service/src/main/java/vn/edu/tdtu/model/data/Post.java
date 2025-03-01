package vn.edu.tdtu.model.data;

import co.elastic.clients.elasticsearch.license.PostResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.dto.TopReacts;
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
public class Post implements Serializable {
    private String id;
    private String content;
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
    private String background;
    private PostResponse sharedPost;
    private GroupInfo groupInfo;
    private List<Media> medias;
    private List<User> taggedUsers;
    private boolean isMine;
    private boolean saved;
    private boolean illegal;
}
