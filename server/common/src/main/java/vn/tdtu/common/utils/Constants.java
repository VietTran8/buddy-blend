package vn.tdtu.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {
    public static final String API_V1_PREFIX = "/api/v1";
    public static final String BEARER_PREFIX = "Bearer ";

    public static class SightEngine {
        public static final String REQUEST_BODY_WORKFLOW = "workflow";
        public static final String REQUEST_BODY_API_USER = "api_user";
        public static final String REQUEST_BODY_API_SECRET = "api_secret";

    }

    public static class Firebase {
        public final static String PROJECT_ID = ResourceUtils.get("fcm.project.id");
        public final static String PROJECT_SENDER_ID = ResourceUtils.get("fcm.sender.id");

        // Firebase Cloud Messaging urls
        public final static String NOTIFICATION_URl = "https://fcm.googleapis.com/fcm/notification";
        public final static String NOTIFICATION_PUBLISH_URL = "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";
        public final static String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

        //Headers for Firebase Cloud Messaging
        public final static String ACCESS_TOKEN_AUTH_HEADER = "access_token_auth";
        public final static String PROJECT_ID_HEADER = "project_id";

        //Query parameters, response/request body for Firebase Cloud Messaging
        public final static String QUERY_PARAM_NOTIFICATION_KEY_NAME = "notification_key_name";
        public final static String RESPONSE_BODY_NOTIFICATION_KEY = "notification_key";
        public final static String RESPONSE_BODY_ERROR = "error";

    }

    public static class Cookie {
        public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    }

    public static class JwtClaims {
        public static final String USER_ID = "user_id";
        public static final String USER_ROLES = "realm_roles";
    }

    public static class RequestHeader {
        public static final String X_FEIGN_USER_ID = "X-Feign-User-Id";
        public static final String X_FEIGN_INTERNAL_HEADER = "X-Feign-Internal";
    }

    public static class KeyCloakUserAttribute {
        public static final String USER_ID = "userId";
    }

    public static class RedisKey {
        public static final String OTP_KEY = "otp";
        public static final String PASSWORD_CHECKING_TOKEN_KEY = "passwordCheckingToken";
        public static final String USER_STATUS_KEY = "status";

        public static String combineKey(String... args) {
            return String.join(":", args);
        }
    }

    public static class SocketProperty {
        public static final String USER_ID = "userId";
        public static final String ROOM_ID = "roomId";
        public static final String SESSION_ID = "sessionId";
    }

    public static class SocketEvent {
        public static final String SEND_MESSAGE = "send_message";
        public static final String JOIN_ROOM = "join_room";
        public static final String EXIT_ALL_ROOM = "exit_rooms";
        public static final String SEEN = "seen";
        public static final String JOINED = "joined";
        public static final String RECIPIENT_SEEN = "recipient_seen";
        public static final String RECEIVE_MESSAGE = "receive_message";
        public static final String NOTIFICATION = "notification";
        public static final String NEW_MESSAGE = "new_message";
        public static final String NEW_POST = "new_post";
    }
}
