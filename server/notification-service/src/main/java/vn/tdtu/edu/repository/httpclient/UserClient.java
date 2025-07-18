package vn.tdtu.edu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.dto.FindByIdsRequest;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = Constants.API_PREFIX + Constants.API_SUB_PREFIX_USER)
public interface UserClient {

    @GetMapping("/by-id/{userId}")
    ResponseVM<UserDTO> findById(@PathVariable("userId") String userId);

    @GetMapping("/by-ids")
    ResponseVM<List<UserDTO>> findByIds(@RequestBody FindByIdsRequest request);
}
