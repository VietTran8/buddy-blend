package vn.edu.tdtu.buddyblend.repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.models.InteractNotification;

public interface INotificationRepository {
    @GET("/api/v1/notifications")
    Call<ResDTO<List<InteractNotification>>> getAll(@Header("Authorization") String token);

    @POST("/api/v1/notifications/detach/{id}")
    Call<ResDTO<Object>> detachNotification(@Header("Authorization") String token, @Path("id") String notificationId);
}
