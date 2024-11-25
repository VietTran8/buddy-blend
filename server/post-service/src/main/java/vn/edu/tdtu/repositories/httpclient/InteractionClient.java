package vn.edu.tdtu.repositories.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.models.Comment;
import vn.edu.tdtu.models.Reacts;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${service.interaction-service.name}", configuration = FeignConfig.class)
public interface InteractionClient {

    @GetMapping("/api/v1/comments")
    public ResDTO<List<Comment>> findCommentsByPostId(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("postId") String postId
    );

    @GetMapping("/api/v1/reacts")
    public ResDTO<Map<EReactionType, List<Reacts>>> findReactionsByPostId(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("postId") String postId
    );

    @GetMapping("/api/v1/comments/count/post/{id}")
    public ResDTO<Long> countCommentByPostId(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") String id
    );
}
