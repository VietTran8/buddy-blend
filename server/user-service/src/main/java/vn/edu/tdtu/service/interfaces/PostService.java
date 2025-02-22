package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.model.data.PostResponse;

import java.util.List;

public interface PostService {
    public List<PostResponse> findByIds(String token, FindByIdsReq reqDTO);
}
