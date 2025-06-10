package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.FileReq;
import vn.edu.tdtu.repository.httpclient.FileClient;
import vn.edu.tdtu.service.intefaces.FileService;
import vn.tdtu.common.enums.post.EFileType;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final FileClient fileClient;

    @Override
    @Async
    public void delete(String url, EFileType type) {
        fileClient.deleteFile(type.getType(), new FileReq(url));
    }
}
