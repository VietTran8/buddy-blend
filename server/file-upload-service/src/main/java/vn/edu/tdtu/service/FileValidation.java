package vn.edu.tdtu.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.enums.EFileUploadStatus;

import java.util.Objects;
import java.util.Set;

@Component
public class FileValidation {
    private static final Set<String> VALID_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "gif",
            "webm", "vob", "flv",
            "ogv", "ogg", "avi", "mov",
            "wmv", "amv", "mp4", "m4p",
            "mpg", "mp2", "m4v"
    );

    public EFileUploadStatus validate(MultipartFile[] files) {
        if ((files.length == 1 && Objects.requireNonNull(files[0].getOriginalFilename()).isEmpty())) {
            return EFileUploadStatus.STATUS_EMPTY_FILE;
        }
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            if (name != null && !name.isEmpty()) {
                if (!isCorrectFormat(file)) {
                    return EFileUploadStatus.STATUS_WRONG_EXT;
                }
            }
        }
        return EFileUploadStatus.STATUS_OK;
    }

    public boolean isCorrectFormat(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && !name.isEmpty()) {
            String[] splited = name.split("\\.");
            String ext = splited[splited.length - 1];

            return VALID_EXTENSIONS.contains(ext.toLowerCase());
        }

        return true;
    }
}
