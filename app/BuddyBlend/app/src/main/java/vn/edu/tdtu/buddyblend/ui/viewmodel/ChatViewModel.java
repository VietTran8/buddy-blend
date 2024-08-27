package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.business.ChatBusiness;
import vn.edu.tdtu.buddyblend.models.Message;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<List<Message>> messages;
    private ChatBusiness chatService;

    public ChatViewModel(){
        chatService = ChatBusiness.getInstance();
        messages = new MutableLiveData<>();
    }
}
