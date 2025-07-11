package vn.edu.tdtu.service.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.message.ExitAllRoomMessage;
import vn.edu.tdtu.message.JoinRoomMessage;
import vn.edu.tdtu.message.SeenMessage;
import vn.edu.tdtu.message.SendMessage;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.JwtUtils;
import vn.tdtu.common.utils.MessageCode;

import java.util.Objects;

@Component
@Slf4j
public class SocketModule {
    private final SocketService socketService;
    private final JwtUtils jwtUtils;

    public SocketModule(SocketIOServer server, JwtUtils jwtUtils, SocketService socketService) {
        this.socketService = socketService;
        this.jwtUtils = jwtUtils;

        server.addConnectListener(onConnected());
        server.addEventListener(Constants.SocketEvent.SEND_MESSAGE, SendMessage.class, onMessageReceived());
        server.addEventListener(Constants.SocketEvent.JOIN_ROOM, JoinRoomMessage.class, onJoinRoom());
        server.addEventListener(Constants.SocketEvent.EXIT_ALL_ROOM, ExitAllRoomMessage.class, onExitAllRoom());
        server.addEventListener(Constants.SocketEvent.SEEN, SeenMessage.class, onSeen());
    }

    public ConnectListener onConnected() {
        return new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
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

                client.set(Constants.SocketProperty.USER_ID, userId);

                log.info("User [{}] with session id {} has connected to chat module", userId, client.getSessionId().toString());
            }
        };
    }

    public DataListener<SendMessage> onMessageReceived() {
        return new DataListener<SendMessage>() {
            @Override
            public void onData(SocketIOClient socketIOClient, SendMessage sendMessage, AckRequest ackRequest) throws Exception {
                log.info(sendMessage.toString());
                socketService.saveMessage(socketIOClient, sendMessage);
            }
        };
    }

    public DataListener<JoinRoomMessage> onJoinRoom() {
        return new DataListener<JoinRoomMessage>() {
            @Override
            public void onData(SocketIOClient client, JoinRoomMessage joinRoomMessage, AckRequest ackRequest) throws Exception {
                socketService.joinRoom(client, joinRoomMessage);
            }
        };
    }

    public DataListener<SeenMessage> onSeen() {
        return new DataListener<SeenMessage>() {
            @Override
            public void onData(SocketIOClient socketIOClient, SeenMessage seenMessage, AckRequest ackRequest) throws Exception {
                String clientUserId = socketIOClient.get(Constants.SocketProperty.USER_ID);

                log.info("User [{}] seen message from room [{}]", clientUserId, seenMessage.getFromUserId());

                socketService.updateSeenTime(
                        socketIOClient,
                        Objects.isNull(clientUserId) ? "" : clientUserId,
                        seenMessage
                );
            }
        };
    }

    public DataListener<ExitAllRoomMessage> onExitAllRoom() {
        return new DataListener<ExitAllRoomMessage>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ExitAllRoomMessage exitAllRoomMessage, AckRequest ackRequest) throws Exception {
                socketService.exitAllRoom(socketIOClient);
            }
        };
    }
}
