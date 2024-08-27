package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import vn.edu.tdtu.buddyblend.business.InteractionBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ShowToastCallback;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.React;

public class ViewReactViewModel extends ViewModel {
    private String postId;
    private InteractionBusiness interactionService;
    private MutableLiveData<Map<EReactionType, List<React>>> reactions;

    public ViewReactViewModel() {
        interactionService = InteractionBusiness.getInstance();
        reactions = new MutableLiveData<>();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public LiveData<Map<EReactionType, List<React>>> getReactions(String token, ShowToastCallback toast){
        interactionService.findReactsByPostId(token, postId, msg -> {
            toast.show(msg);
        }).observeForever(
                data -> reactions.setValue(data)
        );

        return reactions;
    }
}
