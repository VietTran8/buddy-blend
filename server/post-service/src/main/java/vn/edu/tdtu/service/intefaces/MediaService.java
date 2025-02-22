package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.PaginationResponse;
import vn.edu.tdtu.model.Media;

import java.util.List;

public interface MediaService {
    PaginationResponse<Media> getAlbum(String ownerId, int page, int size);
}