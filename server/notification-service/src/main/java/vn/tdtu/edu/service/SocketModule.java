package vn.tdtu.edu.service;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.JwtUtils;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.edu.dto.NewMessageNoti;
import vn.tdtu.edu.message.CommonNotificationMessage;
import vn.tdtu.edu.message.UserConnectMessage;
import vn.tdtu.edu.message.newpost.NewPostMessage;
import vn.tdtu.edu.publisher.KafkaEventPublisher;
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
            String bearerToken = handshakeData.getHttpHeaders().get(HttpHeaders.AUTHORIZATION);

            if (bearerToken == null || !bearerToken.startsWith(Constants.BEARER_PREFIX)) {
                client.disconnect();
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            String token = bearerToken.split(" ")[1];

            if (jwtUtils.isTokenInvalid(token)) {
                client.disconnect();
                throw new UnauthorizedException(MessageCode.Authentication.AUTH_UNAUTHORIZED);
            }

            String userId = jwtUtils.getUserIdFromJwtToken(bearerToken);
            String sessionId = SessionIdUtil.generateSessionId();

            client.set(Constants.SocketProperty.USER_ID, userId);
            client.set(Constants.SocketProperty.SESSION_ID, sessionId);

            publisher.publishUserConnected(new UserConnectMessage(
                    userId,
                    sessionId
            ));

            log.info("User [{}] with session id [{}] has connected to the notification web socket server", userId, client.getSessionId());
        };
    }

    public DisconnectListener onDisconnect() {
        return (client) -> {
            if (client.get(Constants.SocketProperty.USER_ID) != null && client.get(Constants.SocketProperty.SESSION_ID) != null) {
                log.info("User [{}] with session id [{}] has disconnected from the notification web socket server",
                        client.get(Constants.SocketProperty.USER_ID).toString(),
                        client.getSessionId());

                String userId = client.get(Constants.SocketProperty.USER_ID);
                String sessionId = client.get(Constants.SocketProperty.SESSION_ID);

                if (userId != null && sessionId != null)
                    publisher.publishUserDisconnected(new UserConnectMessage(
                            userId,
                            sessionId
                    ));
            }
        };
    }

    public void emitNotification(CommonNotificationMessage notification) {
        server.getAllClients().forEach(c -> {
            if (notification.getToUserIds().get(0).equals(c.get(Constants.SocketProperty.USER_ID))) {
                c.sendEvent(Constants.SocketEvent.NOTIFICATION, notification);
            }
        });
    }

    public void emitChatNotification(NewMessageNoti newMessageNoti) {
        server.getAllClients().forEach(c -> {
            if (newMessageNoti.getToUserId().equals(c.get(Constants.SocketProperty.USER_ID))) {
                log.info("chat notification sent!");
                c.sendEvent(Constants.SocketEvent.NEW_MESSAGE, newMessageNoti);
            }
        });
    }

    public void emitNewPostNotification(NewPostMessage message) {

        server.getAllClients().forEach((client) -> {
            if (message.getBroadcastIds().contains((String) client.get(Constants.SocketProperty.USER_ID))) {
                client.sendEvent(Constants.SocketEvent.NEW_POST, message.getPost());
            }
        });
    }
}
