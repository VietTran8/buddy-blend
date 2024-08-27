package vn.edu.tdtu.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EUserRole;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String id;
    private String email;
    private String userFullName;
    private String password;
    private EUserRole role;
    private Boolean active;
    private String userAvatar;
}
