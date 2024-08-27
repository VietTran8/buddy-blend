package vn.edu.tdtu.buddyblend.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import vn.edu.tdtu.buddyblend.adapters.tagusers.SelectedUserAdapter;
import vn.edu.tdtu.buddyblend.adapters.tagusers.UserFriendsAdapter;
import vn.edu.tdtu.buddyblend.databinding.ActivityTagUserBinding;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.ui.viewmodel.TaggingUserViewModel;
import vn.edu.tdtu.buddyblend.ui.viewmodel.factories.TaggingUserVMFactory;
import vn.edu.tdtu.buddyblend.utils.LayoutManagerUtil;
import vn.edu.tdtu.buddyblend.utils.ScreenManager;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class TagUserActivity extends AppCompatActivity {
    private ActivityTagUserBinding binding;
    private TaggingUserViewModel viewModel;
    private List<MinimizedUser> taggingMinimizedUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ScreenManager.enableFullScreen(getWindow());
        init();
    }

    private void init(){
        initObjects();
        initEvents();

        viewModel.fetchUserFriends();
        viewModel.getFriends().observe(this, friends -> {
            viewModel.getUserFriendsAdapter().setFriends(friends);
            binding.rcvFriends.setLayoutManager(LayoutManagerUtil.disabledScrollLinearManager(TagUserActivity.this, LinearLayoutManager.VERTICAL));
            binding.rcvFriends.setAdapter(viewModel.getUserFriendsAdapter());
            viewModel.getSelectedUserAdapter().setActivity(this);
            viewModel.getSelectedUserAdapter().setUserFriendsAdapter(viewModel.getUserFriendsAdapter());
            binding.rcvSelected.setLayoutManager(new LinearLayoutManager(TagUserActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rcvSelected.setAdapter(viewModel.getSelectedUserAdapter());
            viewModel.getUserFriendsAdapter().setOnUserCheckedListener(new UserFriendsAdapter.OnUserCheckedListener() {
                @Override
                public void onCheck(MinimizedUser minimizedUser, boolean checked) {
                    SelectedUserAdapter adapter = viewModel.getSelectedUserAdapter();
                    if(checked){
                        adapter.addSelectedUser(minimizedUser);
                    }else{
                        adapter.removeSelectedUser(minimizedUser);
                    }

                    Transition transition = new AutoTransition();
                    transition.setDuration(300);
                    TransitionManager.beginDelayedTransition((ViewGroup) binding.selectedContainer.getParent(), transition);
                    if(!adapter.isEmpty()){
                        binding.selectedContainer.setVisibility(View.VISIBLE);
                    }else{
                        binding.selectedContainer.setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    private void initObjects() {
       Intent intent = getIntent();
       String json = intent.getStringExtra("currentTaggedUser");
       Gson gson = new Gson();
       Type typeToken = new TypeToken<List<MinimizedUser>>() {}.getType();
       taggingMinimizedUsers = gson.fromJson(json, typeToken);

       TaggingUserVMFactory factory = new TaggingUserVMFactory(taggingMinimizedUsers);
       viewModel = new ViewModelProvider(this, factory).get(TaggingUserViewModel.class);
       viewModel.setContext(this);
       viewModel.setSharedPreferences(getSharedPreferences(SharedPreferenceKeys.USER_SHARED_PREFS, MODE_PRIVATE));
    }

    private void initEvents(){
        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Gson gson = new Gson();
                intent.putExtra("taggedUsers", gson.toJson(viewModel.getSelectedUserAdapter().getSelectedUser()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void disableSelectedRcv(){
        binding.selectedContainer.setVisibility(View.GONE);
    }
}