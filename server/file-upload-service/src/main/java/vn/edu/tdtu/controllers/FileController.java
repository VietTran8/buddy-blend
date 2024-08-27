package vn.edu.tdtu.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.FileReq;
import vn.edu.tdtu.enums.EUploadFolder;
import vn.edu.tdtu.service.IFileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final IFileService fileService;

    @PostMapping("/upload/{type}")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file, @PathVariable("type") String type){
        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();
        data.put("url", null);
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(data);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if(type.equals("img")){
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            String fileName = fileService.uploadFile(file, folder);

            data.put("url", fileName);
            response.setCode(HttpServletResponse.SC_CREATED);
            response.setData(data);
            response.setMessage("file uploaded successfully");
            return ResponseEntity.ok(response);
        }catch (IOException e){
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/upload-all/{type}")
    public ResponseEntity<?> uploadMultiFile(@RequestParam("files") MultipartFile[] files, @PathVariable("type") String type){
        ResDTO<Map<String, List<String>>> response = new ResDTO<>();
        Map<String, List<String>> data = new HashMap<>();
        data.put("urls", null);
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(data);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if(type.equals("img")){
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            List<String> fileNames = fileService.uploadMultipleFile(files, folder);

            data.put("urls", fileNames);
            response.setCode(HttpServletResponse.SC_CREATED);
            response.setData(data);
            response.setMessage("files uploaded successfully");
            return ResponseEntity.ok(response);
        }catch (IOException e){
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/delete/{type}")
    public ResponseEntity<?> deleteFile(@RequestBody FileReq request, @PathVariable("type") String type){
        ResDTO<Map<String, String>> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if(type.equals("img")){
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            boolean isOk = fileService.deleteFile(request.getUrl(), folder);

            response.setCode(200);
            response.setMessage(isOk ? "file deleted successfully" : "File not found to delete");

        } catch (IOException e) {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update/{type}")
    public ResponseEntity<?> updateFile(@RequestParam("url") String oldUrl, @RequestParam("newFile") MultipartFile newFile, @PathVariable("type") String type){
        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();
        data.put("url", null);
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(data);

        EUploadFolder folder = EUploadFolder.FOLDER_VIDEO;
        if(type.equals("img")){
            folder = EUploadFolder.FOLDER_IMG;
        }
        try {
            String newUrl = fileService.updateFile(oldUrl, newFile, folder);

            data.put("url", newUrl);
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(data);
            response.setMessage("file updated successfully");
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }
}