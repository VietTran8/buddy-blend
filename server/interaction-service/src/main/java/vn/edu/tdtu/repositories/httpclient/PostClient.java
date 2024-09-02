package vn.edu.tdtu.repositories.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.models.Post;

@FeignClient(name = "${service.post-service.name}", configuration = FeignConfig.class)
public interface PostClient {
    @GetMapping("api/v1/posts/{postId}")
    public ResDTO<Post> findById(@RequestHeader("Authorization") String accessToken, @PathVariable("postId") String postId);
}
