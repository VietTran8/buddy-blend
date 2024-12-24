package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;

import java.util.Map;

@FeignClient(name = "${service.file-service.name}", configuration = FeignConfig.class, path = "/api/v1/file")
public interface FileClient {

    @PostMapping( value = "/upload/{fileType}", consumes = "multipart/form-data")
    public ResDTO<Map<String, String>> uploadFile(@PathVariable("fileType") String fileType, @RequestPart("file") MultipartFile file);
}
