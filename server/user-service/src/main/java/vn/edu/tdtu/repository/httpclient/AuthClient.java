package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.tdtu.dto.request.ConfirmTokenCheckingRequest;
import vn.tdtu.common.config.openfeign.FeignConfig;

@FeignClient(name = "${service.auth-service.name}", configuration = FeignConfig.class, path = "/api/v1/auth")
public interface AuthClient {

    @PostMapping("/confirm-token-checking")
    ResponseEntity<?> confirmTokenChecking(@RequestBody ConfirmTokenCheckingRequest request);
}
