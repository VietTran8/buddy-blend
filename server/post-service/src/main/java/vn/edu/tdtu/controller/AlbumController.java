package vn.edu.tdtu.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.PaginationResponse;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.service.intefaces.MediaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
public class AlbumController {
    private final MediaService mediaService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<PaginationResponse<Media>> getAlbum(
            @PathVariable("ownerId") String ownerId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
        PaginationResponse<Media> response = mediaService.getAlbum(ownerId, page, size);

        return ResponseEntity.status(HttpServletResponse.SC_OK).body(response);
    }
}
