package vn.edu.tdtu.repositories.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;

import java.util.Map;

@FeignClient(name = "${service.file-service.name}", configuration = FeignConfig.class)
public interface FileClient {

    @PostMapping( value = "/api/v1/file/upload/{fileType}", consumes = "multipart/form-data")
    public ResDTO<Map<String, String>> uploadFile(@PathVariable("fileType") String fileType, @RequestPart("file") MultipartFile file);
}