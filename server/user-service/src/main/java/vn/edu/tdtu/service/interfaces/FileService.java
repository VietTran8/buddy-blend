package vn.edu.tdtu.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.enums.EFileType;

public interface FileService {
    String upload(MultipartFile file, EFileType type) throws Exception;
}
