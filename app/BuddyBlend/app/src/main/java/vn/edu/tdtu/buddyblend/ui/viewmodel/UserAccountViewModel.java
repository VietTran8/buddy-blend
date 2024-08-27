package vn.edu.tdtu.buddyblend.ui.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.tdtu.buddyblend.business.FileBusiness;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.business.UserBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.UserDetailsResponse;
import vn.edu.tdtu.buddyblend.models.PostResponse;

public class UserAccountViewModel extends ViewModel {
    private final UserBusiness userBusiness;
    private final PostBusiness postBusiness;
    private String currentUserId;
    private UserDetailsResponse currentUser;

    public UserAccountViewModel() {
        userBusiness = UserBusiness.getInstance();
        postBusiness = PostBusiness.getInstance();
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void getUserProfile(String token, ActionCallback<UserDetailsResponse> actionCallback) {
        userBusiness.getUserProfile(token, currentUserId, actionCallback);
    }

    public UserDetailsResponse getCurrentUser() {
        return currentUser;
    }

    public void handleFriendRequest(String token, String toUser, ActionCallback<HandleFriendRequestResponse> actionCallback) {
        userBusiness.handleFriendRequest(token, toUser, actionCallback);
    }

    public void setCurrentUser(UserDetailsResponse currentUser) {
        this.currentUser = currentUser;
    }

    public LiveData<List<PostResponse>> getUserPosts(String token, String userId) {
        return postBusiness.getUserPosts(token, userId);
    }

    public void updateUserAvatar(Activity activity, String token, Uri avatar, ActionCallback<Object> actionCallback) {
        userBusiness.updateUserAvatar(activity, token, avatar, actionCallback);
    }

    public void updateCoverAvatar(Activity activity, String token, Uri avatar, ActionCallback<Object> actionCallback) {
        userBusiness.updateUserCover(activity, token, avatar, actionCallback);
    }
}
