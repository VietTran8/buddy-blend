package vn.edu.tdtu.buddyblend.business;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.FQAcceptationDTO;
import vn.edu.tdtu.buddyblend.dto.request.FriendReqDTO;
import vn.edu.tdtu.buddyblend.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.FriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.RegistrationIdResDTO;
import vn.edu.tdtu.buddyblend.dto.response.UserDetailsResponse;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.IUserRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;
import vn.edu.tdtu.buddyblend.utils.UriUtils;

public class UserBusiness{
    private final IUserRepository userRepository;
    private final String TAG = "UserBusiness";
    private static UserBusiness instance;
    private UserBusiness() {
        userRepository = APIClient.getClient().create(IUserRepository.class);
    }

    public static UserBusiness getInstance(){
        if(instance == null)
            instance = new UserBusiness();
        return instance;
    }

    public void removeRegistrationId(String token, String registrationId, ActionCallback<Object> actionCallback) {
        RegistrationIdResDTO resDTO = new RegistrationIdResDTO(registrationId);

        Call<ResDTO<Object>> call = userRepository.removeUserRegistrationId(token, resDTO);

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

    public void updateUserAvatar(Activity activity, String token, Uri avatar, ActionCallback<Object> actionCallback) {
        File file = new File(UriUtils.getPathFromURI(activity, avatar));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        Call<ResDTO<Object>> call = userRepository.updateUserProfile(token, part);
        call.enqueue(new Callback<ResDTO<Object>>() {
            @Override
            public void onResponse(Call<ResDTO<Object>> call, Response<ResDTO<Object>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess();
                }else{
                    ErrorResponseUtils<ResDTO<Object>, Object> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Object>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });

    }

    public void updateUserCover(Activity activity, String token, Uri avatar, ActionCallback<Object> actionCallback) {
        File file = new File(UriUtils.getPathFromURI(activity, avatar));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        Call<ResDTO<Object>> call = userRepository.updateUserCover(token, part);
        call.enqueue(new Callback<ResDTO<Object>>() {
            @Override
            public void onResponse(Call<ResDTO<Object>> call, Response<ResDTO<Object>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess();
                }else{
                    ErrorResponseUtils<ResDTO<Object>, Object> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Object>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });

    }

    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public LiveData<User> getUser(String token, ActionCallback<MinimizedUser> actionCallback){
        MutableLiveData<User> liveData = new MutableLiveData<>();

        Call<ResDTO<User>> call = userRepository.getUserInfo(token);
        call.enqueue(new Callback<ResDTO<User>>() {
            @Override
            public void onResponse(Call<ResDTO<User>> call, Response<ResDTO<User>> response) {
                if(response.isSuccessful()){
                    liveData.setValue(response.body().getData());
                }else{
                    if(response.code() == 401){
                        actionCallback.onFailure(401, "Phiên đăng nhập hết hạn");
                    }else{
                        actionCallback.onFailure(400, "Có lỗi xảy ra");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResDTO<User>> call, Throwable t) {
                actionCallback.onFailure(400, t.getMessage());
            }
        });

        return liveData;
    }

    public void getUserProfile(String token, String userId, ActionCallback<UserDetailsResponse> actionCallback){
        Call<ResDTO<UserDetailsResponse>> call = userRepository.getUserProfile(token, userId);
        call.enqueue(new Callback<ResDTO<UserDetailsResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<UserDetailsResponse>> call, Response<ResDTO<UserDetailsResponse>> response) {
                if(response.isSuccessful()){
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<UserDetailsResponse>, UserDetailsResponse> responseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(responseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<UserDetailsResponse>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public LiveData<List<MinimizedUser>> getUserFriends(String token, ActionCallback<MinimizedUser> actionCallback){
        MutableLiveData<List<MinimizedUser>> liveData = new MutableLiveData<>();

        Call<ResDTO<List<MinimizedUser>>> call = userRepository.getUserFriends(token);
        call.enqueue(new Callback<ResDTO<List<MinimizedUser>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<MinimizedUser>>> call, Response<ResDTO<List<MinimizedUser>>> response) {
                if(response.isSuccessful()){
                    liveData.setValue(response.body().getData());
                }else{
                    if(response.code() == 401){
                        actionCallback.onFailure(401, "Phiên đăng nhập hết hạn");
                    }else{
                        actionCallback.onFailure(400, "Có lỗi xảy ra");
                    }
                }
            }
            @Override
            public void onFailure(Call<ResDTO<List<MinimizedUser>>> call, Throwable t) {
                actionCallback.onFailure(400, t.getMessage());
            }
        });

        return liveData;
    }

    public LiveData<List<FriendRequestResponse>> getFriendRequest(String token, ActionCallback<MinimizedUser> actionCallback){
        MutableLiveData<List<FriendRequestResponse>> liveData = new MutableLiveData<>();

        Call<ResDTO<List<FriendRequestResponse>>> call = userRepository.getFriendRequests(token);
        call.enqueue(new Callback<ResDTO<List<FriendRequestResponse>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<FriendRequestResponse>>> call, Response<ResDTO<List<FriendRequestResponse>>> response) {
                if(response.isSuccessful()){
                    liveData.setValue(response.body().getData());
                }else{
                    if(response.code() == 401){
                        actionCallback.onFailure(401, "Phiên đăng nhập hết hạn");
                    }else{
                        actionCallback.onFailure(400, "Có lỗi xảy ra");
                    }
                }
            }
            @Override
            public void onFailure(Call<ResDTO<List<FriendRequestResponse>>> call, Throwable t) {
                actionCallback.onFailure(400, t.getMessage());
            }
        });

        return liveData;
    }

    public void saveUserRegistrationId(String token, String registrationId, ActionCallback<RegistrationIdResDTO> actionCallback){
        RegistrationIdResDTO registrationIdResDTO = new RegistrationIdResDTO(registrationId);

        Call<ResDTO<RegistrationIdResDTO>> call = userRepository.saveUserRegistrationId(token, registrationIdResDTO);
        call.enqueue(new Callback<ResDTO<RegistrationIdResDTO>>() {
            @Override
            public void onResponse(Call<ResDTO<RegistrationIdResDTO>> call, Response<ResDTO<RegistrationIdResDTO>> response) {
                if(response.isSuccessful()){
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    actionCallback.onFailure(400,"Something went wrong!");
                }
            }

            @Override
            public void onFailure(Call<ResDTO<RegistrationIdResDTO>> call, Throwable t) {
                actionCallback.onFailure(400,t.getMessage());
            }
        });
    }

    public void handleFriendRequest(String token, String toUserId, ActionCallback<HandleFriendRequestResponse> actionCallback) {
        FriendReqDTO friendReqDTO = new FriendReqDTO(toUserId);

        Call<ResDTO<HandleFriendRequestResponse>> call = userRepository.handleFriendRequest(token, friendReqDTO);
        call.enqueue(new Callback<ResDTO<HandleFriendRequestResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<HandleFriendRequestResponse>> call, Response<ResDTO<HandleFriendRequestResponse>> response) {
                if(response.isSuccessful()){
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<HandleFriendRequestResponse>, HandleFriendRequestResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<HandleFriendRequestResponse>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public void acceptFriendRequest(String token, String requestId, boolean isAccept, ActionCallback<HandleFriendRequestResponse> actionCallback) {
        FQAcceptationDTO fqAcceptationDTO = new FQAcceptationDTO(requestId, isAccept);

        Call<ResDTO<HandleFriendRequestResponse>> call = userRepository.acceptFriendRequest(token, fqAcceptationDTO);
        call.enqueue(new Callback<ResDTO<HandleFriendRequestResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<HandleFriendRequestResponse>> call, Response<ResDTO<HandleFriendRequestResponse>> response) {
                if(response.isSuccessful()){
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<HandleFriendRequestResponse>, HandleFriendRequestResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<HandleFriendRequestResponse>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }
}
