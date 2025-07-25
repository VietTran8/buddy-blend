package vn.edu.tdtu.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.enums.EFileUploadStatus;
import vn.edu.tdtu.enums.EUploadFolder;
import vn.edu.tdtu.service.FileValidation;
import vn.edu.tdtu.service.interfaces.IFileService;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements IFileService {
    private final FileValidation filesValidation;
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile multipartFile, EUploadFolder folder) throws IOException {
        if (multipartFile.isEmpty() || Objects.requireNonNull(multipartFile.getOriginalFilename()).isEmpty()
                || !filesValidation.isCorrectFormat(multipartFile)) {
            return null;
        }

        Map<String, Object> uploadOptions = Map.of("public_id", UUID.randomUUID().toString(),
                "folder", folder.getFolderName(),
                "resource_type", folder.getResourceType(),
                "type", "upload");

        Map<?, ?> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), uploadOptions);

        String publicId = (String) uploadResult.get("public_id");

        return generateOptimizedUrl(publicId, folder);
    }

    @Override
    public List<String> uploadMultipleFile(MultipartFile[] files, EUploadFolder folder) throws IOException {
        EFileUploadStatus eStatus = filesValidation.validate(files);
        if (Objects.requireNonNull(eStatus) == EFileUploadStatus.STATUS_EMPTY_FILE) {
            throw new IOException("Lack of files");
        }
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            fileNames.add(uploadFile(file, folder));
        }
        return fileNames;
    }

    @Override
    public boolean deleteFile(String url, EUploadFolder folder) throws IOException {
        String publicId = parsePublicId(url, folder);
        log.info(publicId);
        if (publicId != null) {
            return cloudinary.uploader()
                    .destroy(publicId, Map.of(
                            "resource_type", folder.getResourceType()
                    )).get("result").equals("ok");
        }
        return false;
    }

    @Override
    public String updateFile(String oldUrl, MultipartFile newFile, EUploadFolder folder) throws IOException {
        if (newFile.isEmpty() || Objects.requireNonNull(newFile.getOriginalFilename()).isEmpty()
                || !filesValidation.isCorrectFormat(newFile)) {
            return null;
        }
        String publicId = parsePublicId(oldUrl, folder);
        return cloudinary.uploader()
                .upload(newFile.getBytes(),
                        Map.of("public_id", publicId != null ? publicId : UUID.randomUUID().toString(),
                                "invalidate", true,
                                "resource_type", "auto"))
                .get("url")
                .toString();
    }

    @Override
    public String parsePublicId(String url, EUploadFolder folder) {
        if (url.contains("http://res.cloudinary.com/dt8itomae")) {
            String[] splitUrl = url.split("/");
            return Strings.concat(
                    folder.getFolderName() + "/",
                    splitUrl[splitUrl.length - 1].split("\\.")[0]
            );
        }
        return null;
    }

    private String generateOptimizedUrl(String publicId, EUploadFolder folder) {
        Transformation<?> transformation = new Transformation<>()
                .width(800).crop("scale")
                .quality("auto")
                .fetchFormat("auto");

        return cloudinary.url().transformation(transformation)
                .resourceType(folder.getResourceType())
                .generate(publicId);
    }
}
