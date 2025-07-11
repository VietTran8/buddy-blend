package vn.tdtu.common.enums.user;

import lombok.Getter;

@Getter
public enum EUserRole {
    ROLE_ADMIN("ADMIN"), ROLE_USER("USER");

    private final String roleName;

    EUserRole(String roleName) {
        this.roleName = roleName;
    }

    public static boolean hasRole(String role) {
        for (EUserRole userRole : EUserRole.values()) {
            if (userRole.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}