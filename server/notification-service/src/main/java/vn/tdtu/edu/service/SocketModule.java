package vn.tdtu.edu.service;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.NewMessageNoti;
import vn.tdtu.edu.exception.UnauthorizedException;
import vn.tdtu.edu.message.CommonNotificationMessage;
import vn.tdtu.edu.message.UserConnectMessage;
import vn.tdtu.edu.message.newpost.NewPostMessage;
import vn.tdtu.edu.publisher.KafkaEventPublisher;
import vn.tdtu.edu.util.JwtUtils;
import vn.tdtu.edu.util.SessionIdUtil;

@Service
@Slf4j
public class SocketModule {
    private final SocketIOServer server;
    private final JwtUtils jwtUtils;
    private final KafkaEventPublisher publisher;

    public SocketModule(SocketIOServer server, JwtUtils jwtUtils, KafkaEventPublisher publisher) {
        this.server = server;
        this.jwtUtils = jwtUtils;
        this.publisher = publisher;

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
            String sessionId = SessionIdUtil.generateSessionId();

            client.set("userId", userId);
            client.set("sessionId", sessionId);

            publisher.publishUserConnected(new UserConnectMessage(
                    userId,
                    sessionId
            ));

            log.info("User [{}] with session id [{}] has connected to the notification web socket server", userId, client.getSessionId());
        };
    }

    public DisconnectListener onDisconnect() {
        return (client) -> {
            if(client.get("userId") != null && client.get("sessionId") != null) {
                log.info("User [{}] with session id [{}] has disconnected from the notification web socket server", client.get("userId").toString(), client.getSessionId());

                String userId = client.get("userId");
                String sessionId = client.get("sessionId");

                if(userId != null && sessionId != null)
                    publisher.publishUserDisconnected(new UserConnectMessage(
                            userId,
                            sessionId
                    ));
            }
        };
    }

    public void emitNotification(CommonNotificationMessage notification) {
        server.getAllClients().forEach(c -> {
                            if(c.get("userId").equals(notification.getToUserIds().get(0))) {
                                c.sendEvent("notification", notification);
                            }
                        });
    }

    public void emitChatNotification(NewMessageNoti newMessageNoti) {
        server.getAllClients().forEach(c -> {
            if(newMessageNoti.getToUserId().equals(c.get("userId"))) {
                log.info("chat notification sent!");
                c.sendEvent("new_message", newMessageNoti);
            }
        });
    }

    public void emitNewPostNotification(NewPostMessage message) {

        server.getAllClients().forEach((client) -> {
            if(message.getBroadcastIds().contains((String) client.get("userId"))) {
                client.sendEvent("new_post", message.getPost());
            }
        });
    }
}
