package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.FindByIdsReq;
import vn.edu.tdtu.model.Post;

import java.util.List;

@FeignClient(name = "${service.post-service.name}", configuration = FeignConfig.class)
public interface PostClient {

    @PostMapping("api/v1/posts/find-all")
    public ResDTO<List<Post>> findAll(@RequestHeader("Authorization") String accessToken, @RequestBody FindByIdsReq requestBody);

}