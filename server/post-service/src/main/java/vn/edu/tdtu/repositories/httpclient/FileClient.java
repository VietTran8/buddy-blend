package vn.edu.tdtu.repositories.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.FileReq;

@FeignClient(name = "${service.file-service.name}", configuration = FeignConfig.class)
public interface FileClient {

    @PostMapping( "/api/v1/file/delete/{fileType}")
    public ResDTO<?> deleteFile(@PathVariable("fileType") String fileType, @RequestBody FileReq requestBody);
}
