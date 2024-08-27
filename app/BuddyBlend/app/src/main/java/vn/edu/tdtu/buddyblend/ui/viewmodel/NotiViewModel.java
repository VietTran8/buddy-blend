package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.business.NotificationBusiness;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.models.InteractNotification;
import vn.edu.tdtu.buddyblend.models.PostResponse;

public class NotiViewModel extends ViewModel {
    private NotificationBusiness notificationService;
    private PostBusiness postService;
    private OnNotificationsFetchedFail onNotificationsFetchedFailListener;

    public interface OnNotificationsFetchedFail {
        void onFetchedFail(String message);
    }

    public NotiViewModel() {
        notificationService = NotificationBusiness.getInstance();
        postService = PostBusiness.getInstance();
    }

    public OnNotificationsFetchedFail getOnNotificationsFetchedFailListener() {
        return onNotificationsFetchedFailListener;
    }

    public LiveData<List<InteractNotification>> getNotifications(String token) {
        return notificationService.fetchNotifications(token, new ActionCallback<Object>() {
            @Override
            public void onFailure(String message) {
                onNotificationsFetchedFailListener.onFetchedFail(message);
            }
        });
    }

    public void detachNotification(String token, String notificationId, ActionCallback<Object> actionCallback) {
        notificationService.detachNotification(token, notificationId, actionCallback);
    }

    public void getPostById(String token, String postId, ActionCallback<PostResponse> actionCallback) {
        postService.findById(token, postId, actionCallback);
    }
}
