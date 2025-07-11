package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier of the group")
    private String id;

    @Schema(description = "Name of the group")
    private String name;

    @Schema(description = "Privacy setting of the group")
    private EGroupPrivacy privacy;

    @Schema(description = "Description of the group")
    private String description;

    @Schema(description = "URL of the group's avatar")
    private String avatar;

    @Schema(description = "URL of the group's cover image")
    private String cover;

    @Schema(description = "Number of members in the group")
    private long memberCount;

    @Schema(description = "Indicates whether the user has joined the group")
    private boolean isJoined;

    @Schema(description = "Identifier of the current member in the group")
    private String currentMemberId;

    @Schema(description = "Join status of the user in the group")
    private EJoinGroupStatus joinStatus;

    @Schema(description = "Indicates whether the group is private")
    private boolean isPrivate;

    @Schema(description = "List of the first ten members of the group")
    private List<GroupMemberDTO> firstTenMembers;

    @Schema(description = "Indicates whether the user is an admin of the group")
    private boolean isAdmin;

    @Schema(description = "Number of pending members in the group")
    private Long pendingMemberCount;

    @Schema(description = "Date and time when the group was created")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Indicates whether the user's membership is pending approval")
    private boolean isPending;
}