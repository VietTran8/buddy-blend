package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

@FeignClient(name = "${service.post-service.name}", configuration = FeignConfig.class, path = Constants.API_PREFIX + Constants.API_SUB_PREFIX_POST)
public interface PostClient {
    @GetMapping("/{postId}")
    ResponseVM<PostDTO> findById(@PathVariable("postId") String postId);
}
