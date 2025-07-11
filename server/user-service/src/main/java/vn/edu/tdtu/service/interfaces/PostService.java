package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.tdtu.common.dto.PostDTO;

import java.util.List;

public interface PostService {
    List<PostDTO> findByIds(String token, FindByIdsReq reqDTO);
}
