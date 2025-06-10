package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.common.enums.group.EGroupPrivacy;
import vn.tdtu.common.enums.group.EJoinGroupStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO implements Serializable {
    private String id;
    private String name;
    private EGroupPrivacy privacy;
    private String description;
    private String avatar;
    private String cover;
    private long memberCount;
    private boolean isJoined;
    private String currentMemberId;
    private EJoinGroupStatus joinStatus;
    private boolean isPrivate;
    private List<GroupMemberDTO> firstTenMembers;
    private boolean isAdmin;
    private Long pendingMemberCount;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private boolean isPending;
}