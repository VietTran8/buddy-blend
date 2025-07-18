package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@FeignClient(name = "${service.post-service.name}", configuration = FeignConfig.class, path = "/api/v1/posts")
public interface PostClient {

    @PostMapping("/find-all")
    ResponseVM<List<PostDTO>> findAll(@RequestBody FindByIdsReq requestBody);

}
