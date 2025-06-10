package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;
import vn.tdtu.common.dto.PostDTO;

@FeignClient(name = "${service.post-service.name}", configuration = FeignConfig.class, path = "/api/v1/posts")
public interface PostClient {
    @GetMapping("/{postId}")
    public ResDTO<PostDTO> findById(@RequestHeader("Authorization") String accessToken, @PathVariable("postId") String postId);
}
