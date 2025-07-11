package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupMemberDTO implements Serializable {

    @Schema(description = "Unique identifier of the group member")
    private String id;

    @Schema(description = "Indicates whether the member is an admin")
    private boolean isAdmin;

    @Schema(description = "Indicates whether the member's request to join is pending")
    private boolean isPending;

    @Schema(description = "The date and time when the member joined the group, formatted as HH:mm:ss dd/MM/yyyy")
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private LocalDateTime joinedAt;

    @Schema(description = "Details of the user who is a member of the group")
    private UserDTO user;


    public GroupMemberDTO(String id, Boolean isAdmin, Boolean isPending, LocalDateTime joinedAt, UserDTO user) {
        this.id = id;
        this.isAdmin = isAdmin;
        this.isPending = isPending;
        this.joinedAt = joinedAt;
        this.user = user;
    }
}
