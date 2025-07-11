package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.User;
import vn.tdtu.common.enums.search.ESyncType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncUserMsg {
    private SyncUser user;
    private ESyncType syncType;

    public static SyncUserMsg fromModel(User user, ESyncType syncType) {
        SyncUserMsg msg = new SyncUserMsg();
        msg.setSyncType(syncType);
        msg.setUser(new SyncUser(
                user.getId(),
                user.getEmail(),
                user.getUserFullName()
        ));

        return msg;
    }
}
