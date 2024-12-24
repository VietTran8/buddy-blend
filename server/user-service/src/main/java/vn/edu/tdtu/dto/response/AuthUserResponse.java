package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EUserRole;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthUserResponse {
    private String id;
    private String email;
    private EUserRole role;
    private Boolean active;
    private String userFullName;
    private String userAvatar;
}
