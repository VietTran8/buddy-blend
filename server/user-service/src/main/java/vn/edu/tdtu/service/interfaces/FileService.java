package vn.edu.tdtu.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.enums.EFileType;

public interface FileService {
    public String upload(MultipartFile file, EFileType type) throws Exception;
}
