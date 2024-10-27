package vn.tdtu.edu.service;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.exception.UnauthorizedException;
import vn.tdtu.edu.model.InteractNotification;
import vn.tdtu.edu.utils.JwtUtils;

@Service
@Slf4j
public class SocketModule {
    private final SocketIOServer server;
    private final JwtUtils jwtUtils;

    public SocketModule(SocketIOServer server, JwtUtils jwtUtils) {
        this.server = server;
        this.jwtUtils = jwtUtils;

        this.server.addConnectListener(onConnect());
    }

    public ConnectListener onConnect() {
        return (client) -> {
            HandshakeData handshakeData = client.getHandshakeData();
            String bearerToken = handshakeData.getHttpHeaders().get("Authorization");

            if(bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                client.disconnect();
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            String token = bearerToken.split(" ")[1];

            if(!jwtUtils.validateJwtToken(token)) {
                client.disconnect();
                throw new UnauthorizedException("You are not authenticated");
            }

            String userId = jwtUtils.getUserIdFromJwtToken(bearerToken);

            client.set("userId", userId);

            log.info("User [{}] has connected to the notification web socket server", client.getSessionId());
        };
    }

    public void emitNotification(InteractNotification notification) {
        server.getAllClients().forEach(c -> {
                            if(c.get("userId").equals(notification.getToUserId())) {
                                c.sendEvent("notification", notification);
                            }
                        });
    }
}
