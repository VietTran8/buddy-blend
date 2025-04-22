package vn.edu.tdtu.constant;

public class MessageCode {
    //User message codes
    public static final String USER_NOT_FOUND = "user.not-found";
    public static final String USER_NOT_FOUND_ID = "user.not-found.id";
    public static final String USER_NOT_FOUND_EMAIL = "user.not-found.email";
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

    //Auth message codes
    public static final String AUTH_UNAUTHORIZED = "auth.unauthorized";
    public static final String AUTH_NOT_PERMITTED = "auth.not-permitted";
    public static final String AUTH_INVALID_TOKEN = "auth.invalid-token";

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
