package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.common.enums.search.ESyncType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyncGroupMsg {
    private String id;
    private String name;
    private ESyncType syncType;
}
