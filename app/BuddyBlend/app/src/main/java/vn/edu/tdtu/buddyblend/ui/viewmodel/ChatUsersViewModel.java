package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.business.ChatBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.models.Room;

public class ChatUsersViewModel extends ViewModel {
    private MutableLiveData<List<Room>> rooms;
    private ChatBusiness chatBusiness;

    public ChatUsersViewModel(){
        chatBusiness = ChatBusiness.getInstance();
        rooms = new MutableLiveData<>();
    }

    public void fetchRooms(String token, ActionCallback<?> callback){
        chatBusiness.getRoomsByUser(token, callback).observeForever(data -> {
            rooms.setValue(data);
        });
    }

    public LiveData<List<Room>> getRooms(){
        return rooms;
    }
}
