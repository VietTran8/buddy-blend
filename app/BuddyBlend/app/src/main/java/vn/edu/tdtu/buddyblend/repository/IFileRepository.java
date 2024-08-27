package vn.edu.tdtu.buddyblend.repository;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import vn.edu.tdtu.buddyblend.dto.ResDTO;

public interface IFileRepository {
    @Multipart
    @POST("/api/v1/file/upload-all/{type}")
    Call<ResDTO<Map<String, List<String>>>> uploadAll(
            @Path("type") String type,
            @Part List<MultipartBody.Part> files
    );
}
