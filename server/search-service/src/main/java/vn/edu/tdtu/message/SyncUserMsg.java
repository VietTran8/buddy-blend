package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.es.SyncUser;
import vn.tdtu.common.enums.search.ESyncType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncUserMsg {
    private SyncUser user;
    private ESyncType syncType;
}
