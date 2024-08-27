package vn.edu.tdtu.buddyblend.ui.viewmodel;

import android.view.View;
import android.widget.Button;

import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.business.InteractionBusiness;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.request.DoReactReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.TopReacts;

public class MediaViewModel extends ViewModel {
    private InteractionBusiness interactionBusiness;
    private PostBusiness postBusiness;
    private View btnPrivacy;
    private EPrivacy currentPrivacy;

    public View getBtnPrivacy() {
        return btnPrivacy;
    }

    public void setBtnPrivacy(View btnPrivacy) {
        this.btnPrivacy = btnPrivacy;
    }

    public EPrivacy getCurrentPrivacy() {
        return currentPrivacy;
    }

    public void setCurrentPrivacy(EPrivacy currentPrivacy) {
        this.currentPrivacy = currentPrivacy;
    }

    public void sharePost(String token, SharePostReqDTO reqDTO, ActionCallback<PostResponse> callback){
        postBusiness.sharePost(token, reqDTO, callback);
    }

    public MediaViewModel() {
        interactionBusiness = InteractionBusiness.getInstance();
        postBusiness = PostBusiness.getInstance();
        currentPrivacy = EPrivacy.PUBLIC;
    }

    public void doReact(String token, String postId, EReactionType reactionType, ActionCallback<List<TopReacts>> actionCallback){
        DoReactReqDTO requestBody = new DoReactReqDTO(postId, reactionType);

        interactionBusiness.doReact(token, requestBody, actionCallback);
    }

    public void deletePost(String token, String postId, ActionCallback<Object> actionCallback){
        postBusiness.deletePost(token, postId, actionCallback);
    }
}
