package vn.edu.tdtu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.user.EUserRole;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String email;
    private String hashedPassword;
    private Boolean active;
    private EUserRole role;
}