package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.edu.tdtu.buddyblend.business.UserBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.User;

public class MyAccountViewModel extends ViewModel {
    private LiveData<User> currentUser;
    private User user;
    private UserBusiness userBusiness;
    private OnCurrentUserFetchedFail onCurrentUserFetched;


    public MyAccountViewModel() {
        userBusiness = UserBusiness.getInstance();
    }

    public void removeRegistrationId(String token, String registrationId, ActionCallback<Object> actionCallback){
        userBusiness.removeRegistrationId(token, registrationId, actionCallback);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public interface OnCurrentUserFetchedFail{
        void onFailure(int statusCode, String message);
    }

    public LiveData<User> getCurrentUser(){
        return currentUser;
    }

    public void fetchUser(String token, OnCurrentUserFetchedFail currentUserFetchedFail){
        this.onCurrentUserFetched = currentUserFetchedFail;

        currentUser = userBusiness.getUser(token, new ActionCallback<MinimizedUser>() {
            @Override
            public void onFailure(Integer status, String message) {
                onCurrentUserFetched.onFailure(status, message);
            }
        });
    }
}
