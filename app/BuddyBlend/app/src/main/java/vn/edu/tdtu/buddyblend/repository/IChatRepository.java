package vn.edu.tdtu.buddyblend.repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.models.Room;

public interface IChatRepository {
    @GET("/api/v1/rooms")
    public Call<ResDTO<List<Room>>> getRoomsByUser(@Header("Authorization") String token);
}
