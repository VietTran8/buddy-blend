package vn.edu.tdtu.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FileReq;
import vn.edu.tdtu.dto.response.UploadFileResponse;
import vn.edu.tdtu.enums.EUploadFolder;
import vn.edu.tdtu.service.interfaces.IFileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final IFileService fileService;

    @PostMapping("/upload/{type}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {
        ResDTO<UploadFileResponse> response = new ResDTO<>();

        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;

        if (type.equals("img")) {
            folder = EUploadFolder.FOLDER_IMG;
        }

        try {
            String fileName = fileService.uploadFile(file, folder);

            UploadFileResponse uploadFileResponse = new UploadFileResponse();

            String publicId = fileService.parsePublicId(fileName, folder);

            uploadFileResponse.setUrl(fileName);
            uploadFileResponse.setThumbnailUrl(folder.equals(EUploadFolder.FOLDER_VIDEO) ? "https://res.cloudinary.com/dt8itomae/video/upload/so_1/v1730141061/buddy-blend/videos/" + publicId + ".jpg" : fileName);

            response.setCode(HttpServletResponse.SC_CREATED);
            response.setData(uploadFileResponse);
            response.setMessage(MessageCode.FILE_UPLOADED);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/upload-all/{type}")
    public ResponseEntity<?> uploadMultiFile(@RequestParam("files") MultipartFile[] files, @PathVariable("type") String type) {
        ResDTO<List<UploadFileResponse>> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);

        EUploadFolder folder;

        if (type.equals("img")) {
            folder = EUploadFolder.FOLDER_IMG;
        } else if (type.equals("vid")) {
            folder = EUploadFolder.FOLDER_VIDEO;
        } else {
            folder = EUploadFolder.FOLDER_OTHERS;
        }

        try {
            List<String> fileNames = fileService.uploadMultipleFile(files, folder);

            List<UploadFileResponse> responses = fileNames.stream().map(name -> {
                log.info(name);
                UploadFileResponse uploadFileResponse = new UploadFileResponse();

                String publicId = fileService.parsePublicId(name, folder);

                uploadFileResponse.setUrl(name);
                uploadFileResponse.setThumbnailUrl(folder.equals(EUploadFolder.FOLDER_VIDEO) ? "https://res.cloudinary.com/dt8itomae/video/upload/so_1/v1730141061/" + publicId + ".jpg" : name);

                return uploadFileResponse;
            }).collect(Collectors.toList());

            response.setCode(HttpServletResponse.SC_CREATED);
            response.setData(responses);
            response.setMessage(MessageCode.FILE_UPLOADED);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/delete/{type}")
    public ResponseEntity<?> deleteFile(@RequestBody FileReq request, @PathVariable("type") String type) {
        log.info("Delete file method called");

        ResDTO<Map<String, String>> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if (type.equals("img")) {
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            boolean isOk = fileService.deleteFile(request.getUrl(), folder);

            response.setCode(200);
            response.setMessage(isOk ? MessageCode.FILE_DELETED : MessageCode.FILE_NOT_FOUND);

        } catch (IOException e) {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update/{type}")
    public ResponseEntity<?> updateFile(@RequestParam("url") String oldUrl, @RequestParam("newFile") MultipartFile newFile, @PathVariable("type") String type) {
        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();
        data.put("url", null);
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(data);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if (type.equals("img")) {
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            String newUrl = fileService.updateFile(oldUrl, newFile, folder);

            data.put("url", newUrl);
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(data);
            response.setMessage(MessageCode.FILE_UPDATED);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }
}