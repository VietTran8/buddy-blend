package vn.tdtu.edu.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class SessionIdUtil {
    public static String generateSessionId() {
        UUID uuid = UUID.randomUUID();

        byte[] uuidBytes = uuid.toString().getBytes(StandardCharsets.UTF_8);

        String shortSessionId = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(uuidBytes);

        return shortSessionId.substring(0, Math.min(shortSessionId.length(), 12));
    }
}
