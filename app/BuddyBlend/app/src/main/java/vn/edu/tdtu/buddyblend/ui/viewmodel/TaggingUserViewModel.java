package vn.edu.tdtu.buddyblend.ui.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.adapters.tagusers.SelectedUserAdapter;
import vn.edu.tdtu.buddyblend.adapters.tagusers.UserFriendsAdapter;
import vn.edu.tdtu.buddyblend.business.UserBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class TaggingUserViewModel extends ViewModel {
    private UserBusiness userBusiness;
    private UserFriendsAdapter userFriendsAdapter;
    private SelectedUserAdapter selectedUserAdapter;
    private SharedPreferences sharedPreferences;
    private MutableLiveData<List<MinimizedUser>> friends;
    private Context context;

    public TaggingUserViewModel(List<MinimizedUser> taggingMinimizedUsers){
        this.userBusiness = UserBusiness.getInstance();
        this.userFriendsAdapter = new UserFriendsAdapter(taggingMinimizedUsers);
        this.selectedUserAdapter = new SelectedUserAdapter();;
        this.selectedUserAdapter.setContext(context);
        this.friends = new MutableLiveData<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void fetchUserFriends(){
        userBusiness.getUserFriends(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), new ActionCallback<MinimizedUser>() {
            @Override
            public void onFailure(Integer status, String message) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }).observeForever(users -> {
            friends.setValue(users);
        });
    }

    public UserFriendsAdapter getUserFriendsAdapter() {
        return userFriendsAdapter;
    }

    public SelectedUserAdapter getSelectedUserAdapter() {
        return selectedUserAdapter;
    }

    public LiveData<List<MinimizedUser>> getFriends(){
        return friends;
    }
}
