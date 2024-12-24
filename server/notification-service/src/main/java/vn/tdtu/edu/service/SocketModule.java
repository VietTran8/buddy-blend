package vn.tdtu.edu.service;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.NewMessageNoti;
import vn.tdtu.edu.exception.UnauthorizedException;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.util.JwtUtils;

@Service
@Slf4j
public class SocketModule {
    private final SocketIOServer server;
    private final JwtUtils jwtUtils;

    public SocketModule(SocketIOServer server, JwtUtils jwtUtils) {
        this.server = server;
        this.jwtUtils = jwtUtils;

        this.server.addConnectListener(onConnect());
        this.server.addDisconnectListener(onDisconnect());
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

            log.info("User [{}] with session id [{}] has connected to the notification web socket server", userId, client.getSessionId());
        };
    }

    public DisconnectListener onDisconnect() {
        return (client) -> {
            log.info("User [{}] with session id [{}] has disconnected from the notification web socket server", client.get("userId").toString(), client.getSessionId());
        };
    }

    public void emitNotification(CommonNotification notification) {
        server.getAllClients().forEach(c -> {
                            if(c.get("userId").equals(notification.getToUserIds().get(0))) {
                                c.sendEvent("notification", notification);
                            }
                        });
    }

    public void emitChatNotification(NewMessageNoti newMessageNoti) {
        server.getAllClients().forEach(c -> {
            if(c.get("userId").equals(newMessageNoti.getToUserId())) {
                log.info("chat notification sent!");
                c.sendEvent("new_message", newMessageNoti);
            }
        });
    }
}
