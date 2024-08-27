package vn.edu.tdtu.buddyblend.business;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.models.InteractNotification;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.INotificationRepository;
import vn.edu.tdtu.buddyblend.repository.ISearchRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;

public class NotificationBusiness {
    private final INotificationRepository notificationRepository;
    private final String TAG = "SearchBusiness";
    private static NotificationBusiness instance;
    private NotificationBusiness() {
        notificationRepository = APIClient.getClient().create(INotificationRepository.class);
    }

    public static NotificationBusiness getInstance(){
        if(instance == null)
            instance = new NotificationBusiness();
        return instance;
    }

    public LiveData<List<InteractNotification>> fetchNotifications(String token, ActionCallback<Object> actionCallback) {
        MutableLiveData<List<InteractNotification>> data = new MutableLiveData<>();

        Call<ResDTO<List<InteractNotification>>> call = notificationRepository.getAll(token);
        call.enqueue(new Callback<ResDTO<List<InteractNotification>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<InteractNotification>>> call, Response<ResDTO<List<InteractNotification>>> response) {
                if(response.isSuccessful()) {
                    data.setValue(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<List<InteractNotification>>, Object> responseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(responseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<List<InteractNotification>>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });

        return data;
    }

    public void detachNotification(String token, String notificationId, ActionCallback<Object> actionCallback) {
        Call<ResDTO<Object>> call = notificationRepository.detachNotification(token, notificationId);
        call.enqueue(new Callback<ResDTO<Object>>() {
            @Override
            public void onResponse(Call<ResDTO<Object>> call, Response<ResDTO<Object>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess();
                }else{
                    ErrorResponseUtils<ResDTO<Object>, Object> responseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(responseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Object>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }
}
