package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.tdtu.common.dto.PostDTO;

import java.util.List;

@FeignClient(name = "${service.post-service.name}", configuration = FeignConfig.class, path = "/api/v1/posts")
public interface PostClient {

    @PostMapping("/find-all")
    public ResDTO<List<PostDTO>> findAll(@RequestHeader("Authorization") String accessToken, @RequestBody FindByIdsReq requestBody);

}
