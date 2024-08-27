package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import vn.edu.tdtu.buddyblend.R;
import vn.edu.tdtu.buddyblend.adapters.chat.ChatUsersAdapter;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.databinding.ActivityViewChatUsersBinding;
import vn.edu.tdtu.buddyblend.ui.viewmodel.ChatUsersViewModel;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class ViewChatUsersActivity extends AppCompatActivity {
    private ActivityViewChatUsersBinding binding;
    private ChatUsersViewModel viewModel;
    private ChatUsersAdapter chatUsersAdapter;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewChatUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE);
        ScreenManager.enableFullScreen(getWindow());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        viewModel = new ViewModelProvider(this).get(ChatUsersViewModel.class);

        init();
    }

    private void init() {
        initObjects();

        viewModel.fetchRooms(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), new ActionCallback<Object>() {
            @Override
            public void onFailure(String message) {
                Toast.makeText(ViewChatUsersActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getRooms().observe(this, data -> {
            chatUsersAdapter.setRooms(data);
        });

        binding.rcvChatUsers.setAdapter(chatUsersAdapter);
        binding.rcvChatUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initObjects() {
        chatUsersAdapter = new ChatUsersAdapter(this);
    }
}