package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.dto.request.FindByUserIdsReq;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = Constants.API_PREFIX + Constants.API_SUB_PREFIX_USER)
public interface UserClient {

    @PostMapping("/by-ids")
    ResponseVM<List<UserDTO>> findByIds(@RequestBody FindByUserIdsReq request);
}