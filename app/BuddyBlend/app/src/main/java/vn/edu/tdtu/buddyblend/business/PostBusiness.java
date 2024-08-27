package vn.edu.tdtu.buddyblend.business;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.CreatePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.FetchNewsFeedReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.UpdatePostContentReqDTO;
import vn.edu.tdtu.buddyblend.dto.response.FetchNewsFeedResDTO;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.IPostRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;

public class PostBusiness {
    private final IPostRepository postRepository;
    private final String TAG = "PostBusiness";
    private static PostBusiness instance;
    private PostBusiness() {
        postRepository = APIClient.getClient().create(IPostRepository.class);
    }

    public static PostBusiness getInstance(){
        if(instance == null)
            instance = new PostBusiness();
        return instance;
    }

    public MutableLiveData<FetchNewsFeedResDTO> fetchNewsFeed(String token, FetchNewsFeedReqDTO reqDTO){
        MutableLiveData<FetchNewsFeedResDTO> data = new MutableLiveData<>();

        Call<ResDTO<FetchNewsFeedResDTO>> call = postRepository.getNewsFeed(token, reqDTO.getPage(), reqDTO.getSize(), reqDTO.getStartTime());
        call.enqueue(new Callback<ResDTO<FetchNewsFeedResDTO>>() {
            @Override
            public void onResponse(Call<ResDTO<FetchNewsFeedResDTO>> call, Response<ResDTO<FetchNewsFeedResDTO>> response) {

                Log.d(TAG, "Status Code: " + response.code());
                if(response.isSuccessful()){

                    FetchNewsFeedResDTO postResponses = response.body().getData();
                    data.setValue(postResponses);
                }else{
                    Log.d(TAG, "fetchNewsFeed: !isSuccessful" );

                    data.setValue(new FetchNewsFeedResDTO());
                }
            }

            @Override
            public void onFailure(Call<ResDTO<FetchNewsFeedResDTO>> call, Throwable t) {
                data.setValue(new FetchNewsFeedResDTO());
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        Log.d(TAG, "onResponse: " + data.getValue());

        return data;
    }

    public MutableLiveData<List<PostResponse>> getUserPosts(String token, String userId){
        MutableLiveData<List<PostResponse>> data = new MutableLiveData<>();

        Call<ResDTO<List<PostResponse>>> call = postRepository.getUserPosts(token, userId);
        call.enqueue(new Callback<ResDTO<List<PostResponse>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<PostResponse>>> call, Response<ResDTO<List<PostResponse>>> response) {

                Log.d(TAG, "Status Code: " + response.code());
                if(response.isSuccessful()){

                    List<PostResponse> postResponses = response.body().getData();
                    data.setValue(postResponses);
                }else{
                    Log.d(TAG, "fetchNewsFeed: !isSuccessful" );

                    data.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ResDTO<List<PostResponse>>> call, Throwable t) {
                data.setValue(new ArrayList<>());
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        Log.d(TAG, "onResponse: " + data.getValue());

        return data;
    }

    public void createPost(String token, CreatePostReqDTO dto, ActionCallback<PostResponse> callback){
        Call<ResDTO<PostResponse>> call = postRepository.createPost(token, dto);

        call.enqueue(new Callback<ResDTO<PostResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<PostResponse>> call, Response<ResDTO<PostResponse>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<PostResponse>, PostResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<PostResponse>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void findById(String token, String postId, ActionCallback<PostResponse> callback) {
        Call<ResDTO<PostResponse>> call = postRepository.findById(token, postId);

        call.enqueue(new Callback<ResDTO<PostResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<PostResponse>> call, Response<ResDTO<PostResponse>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<PostResponse>, PostResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<PostResponse>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void updatePost(String token, UpdatePostContentReqDTO dto, ActionCallback<PostResponse> callback){
        Call<ResDTO<PostResponse>> call = postRepository.updatePost(token, dto);

        call.enqueue(new Callback<ResDTO<PostResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<PostResponse>> call, Response<ResDTO<PostResponse>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<PostResponse>, PostResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<PostResponse>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void deletePost(String token, String postId, ActionCallback<Object> callback){
        Call<ResDTO<Object>> call = postRepository.deletePost(token, postId);

        call.enqueue(new Callback<ResDTO<Object>>() {
            @Override
            public void onResponse(Call<ResDTO<Object>> call, Response<ResDTO<Object>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess();
                }else{
                    ErrorResponseUtils<ResDTO<Object>, Object> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Object>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void sharePost(String token, SharePostReqDTO dto, ActionCallback<PostResponse> callback){
        Call<ResDTO<PostResponse>> call = postRepository.sharePost(token, dto);

        call.enqueue(new Callback<ResDTO<PostResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<PostResponse>> call, Response<ResDTO<PostResponse>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<PostResponse>, PostResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<PostResponse>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}
