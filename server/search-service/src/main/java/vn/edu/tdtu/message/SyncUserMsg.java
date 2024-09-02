package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.model.es.SyncUser;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncUserMsg {
    private SyncUser user;
    private ESyncType syncType;
}
