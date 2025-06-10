package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String id;
    private boolean isAdmin;
    private boolean isPending;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private LocalDateTime joinedAt;
    UserDTO user;

    public GroupMemberDTO(String id, Boolean isAdmin, Boolean isPending, LocalDateTime joinedAt, UserDTO user) {
        this.id = id;
        this.isAdmin = isAdmin;
        this.isPending = isPending;
        this.joinedAt = joinedAt;
        this.user = user;
    }
}
