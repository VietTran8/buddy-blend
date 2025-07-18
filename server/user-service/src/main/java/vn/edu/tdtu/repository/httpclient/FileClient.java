package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.Map;

@FeignClient(name = "${service.file-service.name}", configuration = FeignConfig.class, path = Constants.API_PREFIX + Constants.API_SUB_PREFIX_FILE)
public interface FileClient {

    @PostMapping(value = "/upload/{fileType}", consumes = "multipart/form-data")
    ResponseVM<Map<String, String>> uploadFile(@PathVariable("fileType") String fileType, @RequestPart("file") MultipartFile file);
}
