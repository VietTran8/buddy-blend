package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.model.Media;
import vn.tdtu.common.viewmodel.PaginationResponseVM;

public interface MediaService {
    PaginationResponseVM<Media> getAlbum(String ownerId, int page, int size);
}