package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.content.res.ObbInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.chat.ChatAdapter;
import vn.edu.tdtu.buddyblend.business.ChatSocketModule;
import vn.edu.tdtu.buddyblend.databinding.ActivityChatBinding;
import vn.edu.tdtu.buddyblend.dto.request.SendMessage;
import vn.edu.tdtu.buddyblend.models.Message;
import vn.edu.tdtu.buddyblend.utils.KeyBoardUtils;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityChatBinding binding;
    private ChatSocketModule chatSocketModule;
    private ChatAdapter chatAdapter;
    private String toUserId;
    private String toUserName;
    private String toUserImage;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        ScreenManager.enableFullScreen(getWindow());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        init();
    }

    private void init() {
        toUserId = getIntent().getStringExtra("toUserId");
        toUserName = getIntent().getStringExtra("toUserName");
        toUserImage = getIntent().getStringExtra("toUserImage");

        initObjects();
        bindData();
        initEvents();
    }

    private void initObjects() {
        chatAdapter = new ChatAdapter(toUserImage);
        binding.rcvChats.setAdapter(chatAdapter);
        binding.rcvChats.setLayoutManager(new LinearLayoutManager(this));

        chatSocketModule = new ChatSocketModule(sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""), toUserId, new ChatSocketModule.MessageListener() {
            @Override
            public void onRoomJoined(List<Message> messages, String roomId) {
                runOnUiThread(() -> {
                    chatAdapter.setMessages(messages);
                    scrollToBottom();
                });
            }

            @Override
            public void onMessageReceived(Message message) {
                runOnUiThread(() -> {
                    chatAdapter.addMessage(message);
                    scrollToBottom();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        chatSocketModule.disconnect();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatSocketModule.disconnect();
    }

    private void bindData() {
        if(toUserImage == null || toUserImage.isEmpty()){
            Picasso.get()
                    .load(R.drawable.user)
                    .error(R.color.light_gray)
                    .placeholder(R.color.light_gray)
                    .into(binding.imgAvatar);
        }else{
            Picasso.get()
                    .load(toUserImage)
                    .error(R.color.light_gray)
                    .placeholder(R.color.light_gray)
                    .into(binding.imgAvatar);
        }

        binding.userFullName.setText(toUserName);
    }

    private void initEvents() {
        binding.btnSend.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnSend && !binding.edtInput.getText().toString().isEmpty()) {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setContent(binding.edtInput.getText().toString());
            sendMessage.setFromUserId(sharedPreferences.getString(SharedPreferenceKeys.USER_ID, ""));
            sendMessage.setToUserId(toUserId);
            sendMessage.setImageUrls(new ArrayList<>());

            chatSocketModule.emitSendMessageEvent(sendMessage);

            chatAdapter.addMessage(new Message(sendMessage));
            binding.edtInput.setText("");

            KeyBoardUtils.hideKeyboard(ChatActivity.this);
            scrollToBottom();
        }else if(view == binding.btnBack) {
            onBackPressed();
        }
    }

    private void scrollToBottom() {
        int lastPosition = chatAdapter.getItemCount() - 1;
        binding.rcvChats.scrollToPosition(lastPosition);
    }
}