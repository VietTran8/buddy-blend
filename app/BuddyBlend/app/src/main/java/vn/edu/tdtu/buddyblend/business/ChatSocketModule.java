package vn.edu.tdtu.buddyblend.business;

import android.app.Activity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import vn.edu.tdtu.buddyblend.dto.request.JoinRoomMessage;
import vn.edu.tdtu.buddyblend.dto.request.SendMessage;
import vn.edu.tdtu.buddyblend.models.Message;

public class ChatSocketModule {
    private static final String SOCKET_SERVER = "http://192.168.1.2:8095";
    private Socket socketClient;
    private final MessageListener messageListener;
    private List<Message> messages;

    public ChatSocketModule(String fromUserId, String toUserId, MessageListener messageListener) {
        this.messageListener = messageListener;
        messages = new ArrayList<>();
        connectToSocketServer(fromUserId, toUserId);
    }

    public interface MessageListener {
        void onRoomJoined(List<Message> messages, String roomId);
        void onMessageReceived(Message message);
        void onFailure(String errorMessage);
    }

    private void emitJoinRoomEvent(String fromUserId, String toUserId) {
        JSONObject object = new JSONObject();
        try {
            object.put("fromUserId", fromUserId);
            object.put("toUserId", toUserId);

            socketClient.emit("join_room", object);
        } catch (JSONException e) {
            messageListener.onFailure(e.getMessage());
        }

    }

    public void emitSendMessageEvent(SendMessage message) {
        JSONObject object = new JSONObject();
        JSONArray imageUrl = new JSONArray();
        try {
            message.getImageUrls().forEach(
                    img -> imageUrl.put(img)
            );
            object.put("content", message.getContent());
            object.put("imageUrls", imageUrl);
            object.put("fromUserId", message.getFromUserId());
            object.put("toUserId", message.getToUserId());

            socketClient.emit("send_message", object);
        } catch (JSONException e) {
            messageListener.onFailure(e.getMessage());
        }
    }

    private void connectToSocketServer (String fromUserId, String toUserId) {
        try {
            socketClient = IO.socket(SOCKET_SERVER).connect();
            emitJoinRoomEvent(fromUserId, toUserId);

            startListenEvents();
        } catch (URISyntaxException e) {
            messageListener.onFailure(e.getMessage());
        }
    }

    public void disconnect() {
        if (socketClient != null) {
            socketClient.disconnect();
            socketClient.off();
        }
    }

    private void startListenEvents() {
        socketClient.on("joined", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                JSONArray jsonMessages = data.getJSONArray("messages");
                for (int i = 0; i < jsonMessages.length(); i++) {
                    List<String> imageUrls = new ArrayList<>();
                    JSONObject messageJson = jsonMessages.getJSONObject(i);

                    JSONArray imageUrlsJson = messageJson.getJSONArray("imageUrls");
                    for (int j = 0; j < imageUrlsJson.length(); j++) {
                        imageUrls.add((String) imageUrlsJson.get(i));
                    }

                    Message message = new Message();
                    message.setId(messageJson.getString("id"));
                    message.setContent(messageJson.getString("content"));
                    message.setCreatedAt(messageJson.getString("createdAt"));
                    message.setRead(messageJson.getBoolean("read"));
                    message.setImageUrls(imageUrls);
                    message.setFromUserId(messageJson.getString("fromUserId"));
                    message.setToUserId(messageJson.getString("toUserId"));
                    message.setSentByYou(messageJson.getBoolean("sentByYou"));

                    messages.add(message);
                }

                messageListener.onRoomJoined(messages, data.getString("roomId"));

            } catch (JSONException e) {
                messageListener.onFailure(e.getMessage());
            }
        });

        socketClient.on("receive_message", args -> {
            JSONObject messageJson = (JSONObject) args[0];
            try {
                Message message = new Message();

                message.setId(messageJson.getString("id"));
                message.setContent(messageJson.getString("content"));
                message.setCreatedAt(messageJson.getString("createdAt"));
                message.setRead(messageJson.getBoolean("read"));
                message.setImageUrls(new ArrayList<>());
                message.setFromUserId(messageJson.getString("fromUserId"));
                message.setToUserId(messageJson.getString("toUserId"));
                message.setSentByYou(false);

                messageListener.onMessageReceived(message);
            } catch (JSONException e) {
                messageListener.onFailure(e.getMessage());
            }
        });
    }
}
