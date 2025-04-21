package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class LoginResponse {
    private String id;
    private String username;
    private AuthTokenResponse token;
    private String userFullName;
    private String userAvatar;
    private String email;
}