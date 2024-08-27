package vn.edu.tdtu.dtos.response;

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
    private String password;
    private EUserRole role;
    private Boolean active;
    private String userFullName;
    private String userAvatar;
}
