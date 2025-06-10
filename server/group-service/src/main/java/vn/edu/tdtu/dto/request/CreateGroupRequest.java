package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.group.EGroupPrivacy;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    private String name;
    private String description;
    private EGroupPrivacy privacy;
}
