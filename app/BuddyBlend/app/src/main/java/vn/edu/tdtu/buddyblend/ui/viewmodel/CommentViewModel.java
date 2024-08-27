package vn.edu.tdtu.buddyblend.ui.viewmodel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.tdtu.buddyblend.business.FileBusiness;
import vn.edu.tdtu.buddyblend.business.InteractionBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.callbacks.ShowToastCallback;
import vn.edu.tdtu.buddyblend.dto.request.CreateCommentReqDTO;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.Comment;
import vn.edu.tdtu.buddyblend.models.React;

public class CommentViewModel extends ViewModel {
    private String postId;
    private int postIndex;
    private String userName;
    private String parentCmtId;
    private String reactCount;
    private InteractionBusiness interactService;
    private FileBusiness fileService;
    private MutableLiveData<List<Comment>> comments = new MutableLiveData<>();
    private MutableLiveData<Map<EReactionType, List<React>>> reactions = new MutableLiveData<>();
    private Uri selectedMediaUri;
    private Integer replyCommentIndex;

    public CommentViewModel(){
        this.interactService = InteractionBusiness.getInstance();
        this.fileService = FileBusiness.getInstance();
    }


    //Start Callbacks
    public interface CreateCommentSuccessListener {
        void onSuccess(Comment comment);
    }
    //End Callbacks

    public String getReactCount() {
        return reactCount;
    }

    public void setReactCount(String reactCount) {
        this.reactCount = reactCount;
    }

    public int getPostIndex() {
        return postIndex;
    }

    public void setPostIndex(int postIndex) {
        this.postIndex = postIndex;
    }

    public Integer getReplyCommentIndex() {
        return replyCommentIndex;
    }

    public void setReplyCommentIndex(Integer replyCommentIndex) {
        this.replyCommentIndex = replyCommentIndex;
    }

    public Uri getSelectedMediaUri() {
        return selectedMediaUri;
    }

    public void setSelectedMediaUri(Uri selectedMediaUri) {
        this.selectedMediaUri = selectedMediaUri;
    }

    public String getParentCmtId() {
        return parentCmtId;
    }

    public void setParentCmtId(String parentCmtId) {
        this.parentCmtId = parentCmtId;
    }
    public String getPostId(){
        return this.postId;
    }
    public void setPostId(String postId){
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public LiveData<List<Comment>> getComments(String token, String postId, ShowToastCallback toast){
        interactService.findCommentsByPostId(token, postId, message -> {
            toast.show(message);
        }).observeForever(data -> comments.setValue(data));

        return comments;
    }

    public LiveData<Map<EReactionType, List<React>>> getReactions(String token, String postId, ShowToastCallback toast){
        interactService.findReactsByPostId(token, postId, message -> {
            toast.show(message);
        }).observeForever(data -> reactions.setValue(data));

        return reactions;
    }

    public void addComment(
            Activity activity,
            String token,
            String content,
            ProgressDialog progressDialog,
            ShowToastCallback toast,
            CreateCommentSuccessListener listener
       ){
        progressDialog.show();
        CreateCommentReqDTO requestBody = new CreateCommentReqDTO();
        requestBody.setContent(content);
        requestBody.setImageUrls(new ArrayList<>());
        requestBody.setParentId(this.parentCmtId);
        requestBody.setPostId(this.postId);

        if(selectedMediaUri != null){
            fileService.uploadAll(activity, List.of(selectedMediaUri), "img", new ActionCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> strings) {
                    String url = strings.get(0);
                    requestBody.setImageUrls(List.of(url));

                    doAddComment(token, requestBody, progressDialog, listener, toast);
                }

                @Override
                public void onFailure(String message) {
                    toast.show(message);
                }
            });
        }else{
            doAddComment(token, requestBody, progressDialog, listener, toast);
        }
    }

    private void doAddComment(String token, CreateCommentReqDTO requestBody, ProgressDialog progressDialog, CreateCommentSuccessListener listener, ShowToastCallback toast){
        Log.d("AddCommentBody", "doAddComment: " + requestBody.toString());

        interactService.addComment(token, requestBody, new ActionCallback<Comment>() {
            @Override
            public void onSuccess(Comment comment) {
                progressDialog.dismiss();
                listener.onSuccess(comment);
            }

            @Override
            public void onFailure(String message) {
                progressDialog.dismiss();
                toast.show(message);
            }
        });
    }
}