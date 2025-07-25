package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.Post;
import vn.tdtu.common.enums.search.ESyncType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncPostMsg {
    private SyncPost post;
    private ESyncType syncType;

    public static SyncPostMsg fromModel(Post post, ESyncType syncType) {
        SyncPostMsg msg = new SyncPostMsg();
        msg.setPost(new SyncPost(
                post.getId(),
                post.getContent()
        ));
        msg.setSyncType(syncType);

        return msg;
    }
}