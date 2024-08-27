package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.business.UserBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.FriendRequestResponse;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;

public class FriendViewModel extends ViewModel {
    private MutableLiveData<List<FriendRequestResponse>> friendRequest;
    private UserBusiness userService;
    private OnCurrentUserFetchedFail onCurrentUserFetched;

    public FriendViewModel() {
        userService = UserBusiness.getInstance();
        friendRequest = new MutableLiveData<>();
    }

    public interface OnCurrentUserFetchedFail{
        void onFailure(int statusCode, String message);
    }

    public void setOnCurrentUserFetched(OnCurrentUserFetchedFail onCurrentUserFetched) {
        this.onCurrentUserFetched = onCurrentUserFetched;
    }

    public void getFriendRequest(String token) {
        userService.getFriendRequest(token, new ActionCallback<MinimizedUser>() {
            @Override
            public void onFailure(Integer status, String message) {
                onCurrentUserFetched.onFailure(status, message);
            }
        }).observeForever(request -> friendRequest.setValue(request));
    }

    public LiveData<List<FriendRequestResponse>> getFriendRequest(){
        return this.friendRequest;
    }

    public void handleFriendRequest(String token, String userId, ActionCallback<HandleFriendRequestResponse> actionCallback) {
        userService.handleFriendRequest(token, userId, actionCallback);
    }

    public void acceptFriendRequest(String token, String requestId, boolean isAccept, ActionCallback<HandleFriendRequestResponse> actionCallback) {
        userService.acceptFriendRequest(token, requestId, isAccept, actionCallback);
    }
}
