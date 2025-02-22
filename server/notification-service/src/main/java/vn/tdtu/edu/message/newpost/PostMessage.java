package vn.tdtu.edu.message.newpost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.edu.dto.GroupInfo;
import vn.tdtu.edu.dto.Media;
import vn.tdtu.edu.dto.TopReacts;
import vn.tdtu.edu.enums.EPostType;
import vn.tdtu.edu.enums.EPrivacy;
import vn.tdtu.edu.enums.EReactionType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostMessage {
    private String id;
    private String content;
    private String createdAt;
    private String updatedAt;
    private EPrivacy privacy;
    private EPostType type;
    private UserMessage user;
    private List<Media> medias;
    private int noShared;
    private long noComments;
    private int noReactions;
    private List<TopReacts> topReacts;
    private EReactionType reacted;
    private NewPostMessage sharedPost;
    private String background;
    private GroupInfo groupInfo;
    private List<UserMessage> taggedUsers;
    private boolean isMine;
    private boolean saved;
    private boolean illegal;
}
