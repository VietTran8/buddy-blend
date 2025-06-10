package vn.edu.tdtu.constant;

public class MessageCode {
    public static final String AUTH_LOGIN_SUCCESS = "auth.login-success";
    public static final String AUTH_TOKEN_REFRESHED = "auth.token-refreshed";
    public static final String AUTH_REGISTERED = "auth.registered";
    public static final String AUTH_ADMIN_ROLE_ASSIGNED = "auth.admin-role-assigned";
    public static final String AUTH_ADMIN_ROLE_REVOKED = "auth.admin-role-revoked";
    public static final String AUTH_OTP_SENT = "auth.otp-sent";
    public static final String AUTH_PASSWORD_CHANGED = "auth.password-changed";
    public static final String AUTH_OTP_CORRECT = "auth.otp-correct";
    public static final String AUTH_OTP_INCORRECT = "auth.otp-incorrect";
    public static final String AUTH_MATCH = "auth.match";
    public static final String AUTH_NONE_MATCH = "auth.none-match";
    public static final String AUTH_EMAIL_EXISTS = "auth.email-exists";
    public static final String AUTH_INCORRECT_OLD_PASSWORD = "auth.incorrect-old-password";
    public static final String AUTH_INVALID_CREDENTIALS = "auth.invalid-credentials";
    public static final String AUTH_REFRESH_TOKEN_NOT_FOUND = "auth.refresh-token-not-found";
    public static final String AUTH_INVALID_REFRESH_TOKEN = "auth.invalid-refresh-token";

    public static final String USER_NOT_FOUND = "user.not-found";
    public static final String USER_NOT_FOUND_EMAIL = "user.not-found-email";
    public static final String USER_FETCH_INFO_FAILED = "user.fetch-info-failed";

    public static final String KEYCLOAK_CREATE_USER_FAILED = "keycloak.create-user-failed";
    public static final String KEYCLOAK_UPDATE_USER_FAILED = "keycloak.update-user-failed";
    public static final String KEYCLOAK_ASSIGN_ROLES_FAILED = "keycloak.assign-roles-failed";
    public static final String KEYCLOAK_REMOVE_ROLES_FAILED = "keycloak.remove-roles-failed";
    public static final String KEYCLOAK_EXISTS_AUTH_INFO_BUT_NOT_KC_ACCOUNT = "keycloak.exists-auth-info-but-not-kc-account";
    public static final String KEYCLOAK_RESET_PASSWORD_FAILED = "keycloak.reset-password-failed";
    public static final String KEYCLOAK_CREATE_USER_FAILED_W_STATUS = "keycloak.create-user-failed-w-status";
}
