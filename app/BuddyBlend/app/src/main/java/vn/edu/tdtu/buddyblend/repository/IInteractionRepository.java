package vn.edu.tdtu.buddyblend.repository;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.CreateCommentReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.DoReactReqDTO;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.Comment;
import vn.edu.tdtu.buddyblend.models.React;
import vn.edu.tdtu.buddyblend.models.TopReacts;

public interface IInteractionRepository {
    @POST("/api/v1/reacts")
    Call<ResDTO<List<TopReacts>>> doReact(@Header("Authorization") String token, @Body DoReactReqDTO body);
    @GET("/api/v1/comments")
    Call<ResDTO<List<Comment>>> findCommentsByPostId(@Header("Authorization") String token, @Query("postId") String postId);
    @GET("/api/v1/reacts")
    Call<ResDTO<Map<EReactionType, List<React>>>> findReactsByPostId(@Header("Authorization") String token, @Query("postId") String postId);
    @POST("/api/v1/comments")
    Call<ResDTO<Comment>> addComment(@Header("Authorization") String token, @Body CreateCommentReqDTO requestBody);

}
