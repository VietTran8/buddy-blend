package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.request.FileReq;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.repositories.httpclient.FileClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileClient fileClient;

    @Async
    public void delete(String url, EFileType type) {
        fileClient.deleteFile(type.getType(), new FileReq(url));
    }
}
