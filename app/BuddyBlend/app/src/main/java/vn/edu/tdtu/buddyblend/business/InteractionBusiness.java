package vn.edu.tdtu.buddyblend.business;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.callbacks.OnFetchFailureListener;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.CreateCommentReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.DoReactReqDTO;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.Comment;
import vn.edu.tdtu.buddyblend.models.React;
import vn.edu.tdtu.buddyblend.models.TopReacts;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.IInteractionRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;

public class InteractionBusiness {
    private final IInteractionRepository interactionRepository;
    private final String TAG = "InteractionBusiness";
    private static InteractionBusiness instance;
    private InteractionBusiness() {
        interactionRepository = APIClient.getClient().create(IInteractionRepository.class);
    }

    public static InteractionBusiness getInstance(){
        if(instance == null)
            instance = new InteractionBusiness();
        return instance;
    }

    public void doReact(String token, DoReactReqDTO requestBody, ActionCallback<List<TopReacts>> actionCallback){
        Call<ResDTO<List<TopReacts>>> call = interactionRepository.doReact(token, requestBody);

        call.enqueue(new Callback<ResDTO<List<TopReacts>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<TopReacts>>> call, Response<ResDTO<List<TopReacts>>> response) {
                if(response.isSuccessful()){
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<List<TopReacts>>, List<TopReacts>> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, actionCallback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<List<TopReacts>>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public LiveData<List<Comment>> findCommentsByPostId(String token, String postId, OnFetchFailureListener listener){
        MutableLiveData<List<Comment>> data = new MutableLiveData<>();

        Call<ResDTO<List<Comment>>> call = interactionRepository.findCommentsByPostId(token, postId);

        call.enqueue(new Callback<ResDTO<List<Comment>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<Comment>>> call, Response<ResDTO<List<Comment>>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body().getData());
                }else{
                    data.setValue(new ArrayList<>());
                    ErrorResponseUtils<ResDTO<List<Comment>>, ?> utils = new ErrorResponseUtils<>();
                    listener.onFetchFailure(utils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<List<Comment>>> call, Throwable t) {
                data.setValue(new ArrayList<>());
                listener.onFetchFailure(t.getMessage());
            }
        });

        return data;
    }

    public LiveData<Map<EReactionType, List<React>>> findReactsByPostId(String token, String postId, OnFetchFailureListener onFetchFailureListener){
        MutableLiveData<Map<EReactionType, List<React>>> data = new MutableLiveData<>();

        Call<ResDTO<Map<EReactionType, List<React>>>> call = interactionRepository.findReactsByPostId(token, postId);

        call.enqueue(new Callback<ResDTO<Map<EReactionType, List<React>>>>() {
            @Override
            public void onResponse(Call<ResDTO<Map<EReactionType, List<React>>>> call, Response<ResDTO<Map<EReactionType, List<React>>>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<Map<EReactionType, List<React>>>, ?> utils = new ErrorResponseUtils<>();
                    onFetchFailureListener.onFetchFailure(utils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Map<EReactionType, List<React>>>> call, Throwable t) {
                onFetchFailureListener.onFetchFailure(t.getMessage());
            }
        });

        return data;
    }

    public void addComment(String token, CreateCommentReqDTO requestBody, ActionCallback<Comment> actionCallback){
        Call<ResDTO<Comment>> call = interactionRepository.addComment(token, requestBody);

        call.enqueue(new Callback<ResDTO<Comment>>() {
            @Override
            public void onResponse(Call<ResDTO<Comment>> call, Response<ResDTO<Comment>> response) {
                if(response.isSuccessful()){
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<Comment>, ?> utils = new ErrorResponseUtils<>();
                    String errorMsg = utils.getErrorResponseMessage(response);
                    actionCallback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Comment>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }
}
