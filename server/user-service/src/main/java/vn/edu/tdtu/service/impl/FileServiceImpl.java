package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.repository.httpclient.FileClient;
import vn.edu.tdtu.service.interfaces.FileService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final FileClient fileClient;

    @Override
    public String upload(MultipartFile file, EFileType type) throws Exception {
        ResDTO<Map<String, String>> response = fileClient.uploadFile(type.getType(), file);

        return response.getData().get("url");
    }
}
