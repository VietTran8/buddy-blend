package vn.edu.tdtu.buddyblend.repository;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.CreatePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.FetchNewsFeedReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.UpdatePostContentReqDTO;
import vn.edu.tdtu.buddyblend.dto.response.FetchNewsFeedResDTO;
import vn.edu.tdtu.buddyblend.models.PostResponse;

public interface IPostRepository {
    @GET("/api/v1/posts/news-feed")
    public Call<ResDTO<FetchNewsFeedResDTO>> getNewsFeed(
             @Header("Authorization") String token,
             @Query("page") int page,
             @Query("size") int size,
             @Query("startTime") String startTime
    );
    @POST("/api/v1/posts")
    public Call<ResDTO<PostResponse>> createPost(@Header("Authorization") String token, @Body CreatePostReqDTO req);
    @GET("/api/v1/posts")
    public Call<ResDTO<List<PostResponse>>> getUserPosts(@Header("Authorization") String token, @Query("userId") String userId);
    @GET("/api/v1/posts/{id}")
    public Call<ResDTO<PostResponse>> findById(@Header("Authorization") String token, @Path("id") String postId);
    @POST("/api/v1/posts/update")
    public Call<ResDTO<PostResponse>> updatePost(@Header("Authorization") String token, @Body UpdatePostContentReqDTO req);
    @POST("/api/v1/posts/delete/{id}")
    public Call<ResDTO<Object>> deletePost(@Header("Authorization") String token, @Path("id") String id);
    @POST("/api/v1/posts/share")
    public Call<ResDTO<PostResponse>> sharePost(@Header("Authorization") String token, @Body SharePostReqDTO req);
}
