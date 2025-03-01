package vn.edu.tdtu.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EGroupPrivacy;
import vn.edu.tdtu.enums.EJoinGroupStatus;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Serializable {
    private String id;
    private String name;
    private String avatar;
    private String description;
    private long memberCount;
    private EGroupPrivacy privacy;
    private boolean isJoined;
    private String currentMemberId;
    private EJoinGroupStatus joinStatus;
}
