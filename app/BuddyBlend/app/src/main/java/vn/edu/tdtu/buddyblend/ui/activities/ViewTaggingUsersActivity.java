package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import vn.edu.tdtu.buddyblend.adapters.homefragment.TaggingUserAdapter;
import vn.edu.tdtu.buddyblend.databinding.ActivityViewTaggingUsersBinding;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;

public class ViewTaggingUsersActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityViewTaggingUsersBinding binding;
    private TaggingUserAdapter taggingUserAdapter;
    private List<MinimizedUser> taggingUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewTaggingUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ScreenManager.enableFullScreen(this.getWindow());

        init();
    }

    public void init(){
        bindTaggingUsers();
        initObjects();
        initEvents();

        binding.rcvTaggingUsers.setAdapter(taggingUserAdapter);
        binding.rcvTaggingUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    public void bindTaggingUsers(){
        String taggingUsersJson = getIntent().getStringExtra("users");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<MinimizedUser>>() {}.getType();
        taggingUsers = gson.fromJson(taggingUsersJson, listType);
    }

    public void initObjects(){
        taggingUserAdapter = new TaggingUserAdapter();
        taggingUserAdapter.setUsers(taggingUsers);
    }

    public void initEvents(){
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnBack){
            onBackPressed();
        }
    }
}