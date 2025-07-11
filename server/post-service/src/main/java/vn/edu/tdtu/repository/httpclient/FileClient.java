package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.tdtu.dto.request.FileReq;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.viewmodel.ResponseVM;

@FeignClient(name = "${service.file-service.name}", configuration = FeignConfig.class, path = "/api/v1/file")
public interface FileClient {

    @PostMapping("/delete/{fileType}")
    ResponseVM<?> deleteFile(@PathVariable("fileType") String fileType, @RequestBody FileReq requestBody);
}
