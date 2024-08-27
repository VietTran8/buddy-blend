package vn.edu.tdtu.buddyblend.repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.response.SearchHistory;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;

public interface ISearchRepository {
    @GET("/api/v1/search")
    public Call<ResDTO<SearchResponse>> doSearch(@Header("Authorization") String token, @Query("key") String key);
    @GET("/api/v1/search/fetch")
    public Call<ResDTO<SearchResponse>> fetchResult(@Header("Authorization") String token, @Query("key") String key);
    @GET("/api/v1/search/history")
    public Call<ResDTO<List<SearchHistory>>> getSearchHistory(@Header("Authorization") String token);
    @POST("/api/v1/search/history/delete/{id}")
    public Call<ResDTO<Object>> deleteSearchHistory(@Header("Authorization") String token, @Path("id") String historyId);
    @POST("/api/v1/search/history/clear")
    public Call<ResDTO<Object>> clearSearchHistory(@Header("Authorization") String token);
}
