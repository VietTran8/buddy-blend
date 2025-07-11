package vn.tdtu.common.utils;

public class MessageCode {

    public static class User {
        //User message codes
        public static final String USER_NOT_FOUND = "user.not-found";
        public static final String USER_NOT_FOUND_ID = "user.not-found.id";
        public static final String USER_NOT_FOUND_EMAIL = "user.not-found.email";
        public static final String USER_FETCH_INFO_FAILED = "user.fetch-info-failed";
        public static final String USER_ID_NOT_NULL = "user.id-not-null";
        public static final String USER_EMAIL_EXISTS = "user.email-exists";
        public static final String USER_FETCHED = "user.fetched";
        public static final String USER_SAVED = "user.saved";
        public static final String USER_UPDATED = "user.updated";
        public static final String USER_DISABLED = "user.disabled";
        public static final String USER_SAVED_REGISTRATION_ID = "user.saved-registration-id";
        public static final String USER_DELETED_REGISTRATION_ID = "user.deleted-registration-id";

        //User favourite message codes
        public static final String USER_FAVOURITE_NOT_FOUND = "user-favourite.not-found";
        public static final String USER_FAVOURITE_FETCHED = "user-favourite.fetched";
        public static final String USER_FAVOURITE_DELETED = "user-favourite.deleted";

        //Blocking message codes
        public static final String BLOCKING_OPPONENT_BLOCKED = "blocking.opponent-blocked";
        public static final String BLOCKING_BLOCKED = "blocking.blocked";
        public static final String BLOCKING_UNBLOCKED = "blocking.unblocked";
        public static final String BLOCKING_FETCHED = "blocking.fetched";

        //Friend request message codes
        public static final String FRIEND_REQUEST_CAN_NOT_SEND = "friend-request.can-not-send";
        public static final String FRIEND_REQUEST_NOT_FOUND = "friend-request.not-found";
        public static final String FRIEND_REQUEST_JUST_HANDLE_PENDING = "friend-request.just-handle-pending";
        public static final String FRIEND_REQUEST_CAN_NOT_SEND_TO_USER_ID = "friend-request.can-not-send.to-user-id";
        public static final String FRIEND_REQUEST_FRIEND_LIST_FETCHED = "friend-request.friend-list-fetched";
        public static final String FRIEND_REQUEST_FETCHED = "friend-request.fetched";
        public static final String FRIEND_REQUEST_SUGGESTION_FETCHED = "friend-request.suggestion-fetched";
        public static final String FRIEND_REQUEST_REJECTED = "friend-request.rejected";
        public static final String FRIEND_REQUEST_ACCEPTED = "friend-request.accepted";
        public static final String FRIEND_REQUEST_SENT = "friend-request.sent";
        public static final String FRIEND_REQUEST_UNFRIENDED = "friend-request.unfriended";
        public static final String FRIEND_REQUEST_CANCELLED = "friend-request.cancelled";

    }

    public static class Story {
        //Story messages
        public static final String STORY_CREATED = "story.created";
        public static final String STORY_DELETED = "story.deleted";
        public static final String STORY_FETCHED = "story.fetched";
        public static final String STORY_NOT_FOUND = "story.not-found";
        public static final String STORY_CAN_NOT_DELETE_OTHER = "story.can-not-delete-others";
        public static final String STORY_CAN_NOT_SELF_REACT = "story.can-not-self-react";

        //Viewer messages
        public static final String VIEWER_ALREADY_EXISTS = "viewer.already-exists";
        public static final String VIEWER_COUNTED = "viewer.counted";
        public static final String VIEWER_FETCHED = "viewer.fetched";
        public static final String VIEWER_NOT_FOUND = "viewer.not-found";

        //Reaction messages
        public static final String REACTION_CREATED = "reaction.created";
    }

    public static class Search {
        public static final String SEARCH_SEARCHED = "search.searched";
        public static final String SEARCH_RESULT_FETCHED = "search.result-fetched";
        public static final String SEARCH_HISTORY_FETCHED = "search.history-fetched";
        public static final String SEARCH_HISTORY_DELETED = "search.history-deleted";
    }

    public static class Post {
        public static final String POST_FETCHED = "post.fetched";
        public static final String POST_NOT_FOUND_ID = "post.not-found-id";
        public static final String POST_SAVED = "post.saved";
        public static final String POST_UPDATED = "post.updated";
        public static final String POST_DELETED = "post.deleted";
        public static final String POST_SHARED = "post.shared";
        public static final String POST_CAN_NOT_DELETE_OTHERS = "post.can-not-delete-others";
        public static final String POST_SAVE_POST = "post.save-post";
        public static final String POST_UNSAVE_POST = "post.unsave-post";
        public static final String POST_NOT_FOUND = "post.not-found";
        public static final String POST_CAN_NOT_UPDATE_OTHERS = "post.can-not-update-others";
        public static final String POST_ACCEPTED = "post.accepted";
        public static final String POST_DETACHED = "post.detached";

        public static final String REPORT_SUBMITTED = "report.submitted";
        public static final String REPORT_FETCHED = "report.fetched";
        public static final String REPORT_NOT_FOUND = "report.not-found";

        public static final String GROUP_NOT_PERMITTED = "group.not-permitted";

        public static final String BANNED_WORD_DELETED = "banned-word.deleted";
        public static final String BANNED_WORD_SAVED = "banned-word.saved";
        public static final String BANNED_WORD_NOT_FOUND_WORD = "banned-word.not-found-word";
    }

    public static class Notification {
        public static final String NOTIFICATION_FETCHED = "notification.fetched";
        public static final String NOTIFICATION_DETACHED = "notification.detached";
        public static final String NOTIFICATION_READ = "notification.read";
        public static final String NOTIFICATION_NOT_FOUND = "notification.not-found";
        public static final String NOTIFICATION_NOT_PERMITTED = "notification.not-permitted";

        public static final String VIOLATION_FETCHED = "violation.fetched";
        public static final String VIOLATION_NOT_FOUND = "violation.not-found";
    }

    public static class Message {
        public static final String ROOM_NOT_FOUND = "room.not-found";
        public static final String ROOM_FETCHED = "room.fetched";
        public static final String MESSAGE_FETCHED = "message.fetched";
    }

    public static class Interaction {
        public static final String REACTION_CREATED = "reaction.created";
        public static final String REACTION_UNCREATED = "reaction.uncreated";
        public static final String REACTION_UPDATED = "reaction.updated";
        public static final String REACTION_FETCHED = "reaction.fetched";

        public static final String COMMENT_DELETED = "comment.deleted";
        public static final String COMMENT_CREATED = "comment.created";
        public static final String COMMENT_UPDATED = "comment.updated";
        public static final String COMMENT_CAN_NOT_DELETE_OTHERS = "comment.can-not-delete-others";
        public static final String COMMENT_NOT_FOUND_ID = "comment.not-found-id";
        public static final String COMMENT_FETCHED = "comment.fetched";

    }

    public static class Group {
        public final static String GROUP_CREATED_SUCCESS = "group.created-success";
        public final static String GROUP_UPDATED_SUCCESS = "group.updated-success";
        public final static String GROUP_DELETED_SUCCESS = "group.deleted-success";
        public final static String GROUP_FETCHED = "group.fetched";
        public final static String GROUP_NOT_FOUND = "group.not-found";
        public static final String GROUP_NOT_FOUND_ID = "group.not-found-id";
        public final static String GROUP_ALREADY_JOINED = "group.already-joined";
        public final static String GROUP_JOIN_PENDING = "group.join-pending";
        public final static String GROUP_JOINED = "group.joined";
        public final static String GROUP_IS_PUBLIC = "group.is-public";
        public final static String GROUP_IS_PRIVATE = "group.is-private";
        public final static String GROUP_NOT_PERMITTED = "group.not-permitted";
        public final static String GROUP_MEMBER_FETCHED = "group.member-fetched";
        public final static String GROUP_MEMBER_NOT_FOUND = "group.member-not-found";
        public final static String GROUP_MEMBER_ACCEPTED = "group.member-accepted";
        public final static String GROUP_MEMBER_REJECTED = "group.member-rejected";
        public final static String GROUP_MEMBER_DELETED = "group.member-deleted";
        public final static String GROUP_LEAVED = "group.leaved";
        public final static String GROUP_PENDING_CANCELLED = "group.pending-cancelled";
        public final static String GROUP_MEMBER_JOINED = "group.member-joined";
        public final static String GROUP_MEMBER_PROMOTED = "group.member-promoted";
        public final static String GROUP_MEMBER_REVOKED = "group.member-revoked";
    }

    public static class File {
        public static final String FILE_UPLOADED = "file.uploaded";
        public static final String FILE_DELETED = "file.deleted";
        public static final String FILE_NOT_FOUND = "file.not-found";
        public static final String FILE_UPDATED = "file.updated";
    }

    public static class Authentication {
        public static final String AUTH_UNAUTHORIZED = "auth.unauthorized";
        public static final String AUTH_NOT_PERMITTED = "auth.not-permitted";
        public static final String AUTH_INVALID_TOKEN = "auth.invalid-token";
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

        public static final String KEYCLOAK_CREATE_USER_FAILED = "keycloak.create-user-failed";
        public static final String KEYCLOAK_UPDATE_USER_FAILED = "keycloak.update-user-failed";
        public static final String KEYCLOAK_ASSIGN_ROLES_FAILED = "keycloak.assign-roles-failed";
        public static final String KEYCLOAK_REMOVE_ROLES_FAILED = "keycloak.remove-roles-failed";
        public static final String KEYCLOAK_EXISTS_AUTH_INFO_BUT_NOT_KC_ACCOUNT = "keycloak.exists-auth-info-but-not-kc-account";
        public static final String KEYCLOAK_RESET_PASSWORD_FAILED = "keycloak.reset-password-failed";
        public static final String KEYCLOAK_CREATE_USER_FAILED_W_STATUS = "keycloak.create-user-failed-w-status";
        public static final String KEYCLOAK_LOGOUT_FAILED = "keycloak.logout-failed";
    }

    public static class Exception {
        public static final String EXCEPTION_NULL_POINTER = "exception.null-pointer";
        public static final String EXCEPTION_ILLEGAL_ARGUMENT = "exception.illegal-argument";
        public static final String EXCEPTION_OUT_OF_BOUNDS = "exception.out-of-bounds";
        public static final String EXCEPTION_ILLEGAL_STATE = "exception.illegal-state";
        public static final String EXCEPTION_UNKNOWN = "exception.unknown";
    }

}
