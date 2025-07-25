package vn.edu.tdtu.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.service.intefaces.MediaService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.PaginationResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_POST + "/album")
@RequiredArgsConstructor
public class AlbumController {
    private final MediaService mediaService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<PaginationResponseVM<Media>> getAlbum(
            @PathVariable("ownerId") String ownerId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        PaginationResponseVM<Media> response = mediaService.getAlbum(ownerId, page, size);

        return ResponseEntity.status(HttpServletResponse.SC_OK).body(response);
    }
}
