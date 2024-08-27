package vn.edu.tdtu.buddyblend.repository;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.FQAcceptationDTO;
import vn.edu.tdtu.buddyblend.dto.request.FriendReqDTO;
import vn.edu.tdtu.buddyblend.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.FriendRequestResponse;
import vn.edu.tdtu.buddyblend.dto.response.RegistrationIdResDTO;
import vn.edu.tdtu.buddyblend.dto.response.UserDetailsResponse;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.User;

public interface IUserRepository {
    @GET("/api/v1/users/profile")
    Call<ResDTO<User>> getUserInfo(@Header("Authorization") String token);

    @GET("/api/v1/users/friends")
    Call<ResDTO<List<MinimizedUser>>> getUserFriends(@Header("Authorization") String token);

    @Multipart
    @POST("/api/v1/users/profile/update")
    Call<ResDTO<Object>> updateUserProfile(@Header("Authorization") String token, @Part MultipartBody.Part file);

    @Multipart
    @POST("/api/v1/users/cover/update")
    Call<ResDTO<Object>> updateUserCover(@Header("Authorization") String token, @Part MultipartBody.Part file);

    @GET("/api/v1/users/friend-reqs")
    Call<ResDTO<List<FriendRequestResponse>>> getFriendRequests(@Header("Authorization") String token);

    @GET("/api/v1/users/profile")
    Call<ResDTO<UserDetailsResponse>> getUserProfile(@Header("Authorization") String token, @Query("id") String userId);

    @POST("/api/v1/users/friend-req/acceptation")
    Call<ResDTO<HandleFriendRequestResponse>> acceptFriendRequest(@Header("Authorization") String token, @Body FQAcceptationDTO requestBody);

    @POST("/api/v1/users/friend-req")
    Call<ResDTO<HandleFriendRequestResponse>> handleFriendRequest(@Header("Authorization") String token, @Body FriendReqDTO requestBody);

    @POST("/api/v1/users/registration/save")
    Call<ResDTO<RegistrationIdResDTO>> saveUserRegistrationId(@Header("Authorization") String token, @Body RegistrationIdResDTO requestBody);

    @POST("/api/v1/users/registration/remove")
    Call<ResDTO<Object>> removeUserRegistrationId(@Header("Authorization") String token, @Body RegistrationIdResDTO requestBody);
}
