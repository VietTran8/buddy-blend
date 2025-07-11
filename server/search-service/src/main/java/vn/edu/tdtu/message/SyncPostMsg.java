package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.es.SyncPost;
import vn.tdtu.common.enums.search.ESyncType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncPostMsg {
    private SyncPost post;
    private ESyncType syncType;
}