package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.repositories.httpclient.FileClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileClient fileClient;

    public String upload(MultipartFile file, EFileType type) throws Exception {
        ResDTO<Map<String, String>> response = fileClient.uploadFile(type.getType(), file);

        return response.getData().get("url");
    }
}
