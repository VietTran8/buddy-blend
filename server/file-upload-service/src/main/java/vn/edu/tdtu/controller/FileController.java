package vn.edu.tdtu.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.request.FileReq;
import vn.edu.tdtu.dto.response.UploadFileResponse;
import vn.edu.tdtu.enums.EUploadFolder;
import vn.edu.tdtu.service.interfaces.IFileService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_FILE)
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final IFileService fileService;

    @PostMapping("/upload/{type}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {
        ResponseVM<UploadFileResponse> response = new ResponseVM<>();

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
            response.setMessage(MessageCode.File.FILE_UPLOADED);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/upload-all/{type}")
    public ResponseEntity<?> uploadMultiFile(@RequestParam("files") MultipartFile[] files, @PathVariable("type") String type) {
        ResponseVM<List<UploadFileResponse>> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);

        EUploadFolder folder;

        if ("img".equals(type)) {
            folder = EUploadFolder.FOLDER_IMG;
        } else if ("vid".equals(type)) {
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
            response.setMessage(MessageCode.File.FILE_UPLOADED);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/delete/{type}")
    public ResponseEntity<?> deleteFile(@RequestBody FileReq request, @PathVariable("type") String type) {
        log.info("Delete file method called");

        ResponseVM<Map<String, String>> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if ("img".equals(type)) {
            folder = EUploadFolder.FOLDER_IMG;
        }

        try {
            boolean isOk = fileService.deleteFile(request.getUrl(), folder);

            response.setCode(200);
            response.setMessage(isOk ? MessageCode.File.FILE_DELETED : MessageCode.File.FILE_NOT_FOUND);

        } catch (IOException e) {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update/{type}")
    public ResponseEntity<?> updateFile(@RequestParam("url") String oldUrl, @RequestParam("newFile") MultipartFile newFile, @PathVariable("type") String type) {
        ResponseVM<Map<String, String>> response = new ResponseVM<>();
        Map<String, String> data = new HashMap<>();
        data.put("url", null);
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(data);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if ("img".equals(type)) {
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            String newUrl = fileService.updateFile(oldUrl, newFile, folder);

            data.put("url", newUrl);
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(data);
            response.setMessage(MessageCode.File.FILE_UPDATED);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }
}