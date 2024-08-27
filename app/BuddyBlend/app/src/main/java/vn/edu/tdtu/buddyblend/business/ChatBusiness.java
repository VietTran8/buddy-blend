package vn.edu.tdtu.buddyblend.business;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.JoinRoomMessage;
import vn.edu.tdtu.buddyblend.models.Message;
import vn.edu.tdtu.buddyblend.models.Room;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.IChatRepository;
import vn.edu.tdtu.buddyblend.repository.IFileRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;

public class ChatBusiness {
    private final IChatRepository chatRepository;
    private final String TAG = "ChatBusiness";
    private static ChatBusiness instance;
    private ChatBusiness() {
        chatRepository = APIClient.getClient().create(IChatRepository.class);
    }

    public static ChatBusiness getInstance(){
        if(instance == null)
            instance = new ChatBusiness();
        return instance;
    }
    public LiveData<List<Room>> getRoomsByUser(String token, ActionCallback<?> callback){
        MutableLiveData<List<Room>> data = new MutableLiveData<>();

        Call<ResDTO<List<Room>>> call = chatRepository.getRoomsByUser(token);

        call.enqueue(new Callback<ResDTO<List<Room>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<Room>>> call, Response<ResDTO<List<Room>>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<List<Room>>, ?> utils = new ErrorResponseUtils<>();
                    callback.onFailure(utils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<List<Room>>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });

        return data;
    }

}
