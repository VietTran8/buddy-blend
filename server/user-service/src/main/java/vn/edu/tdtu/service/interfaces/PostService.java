package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    public List<PostResponse> findByIds(String token, FindByIdsReq reqDTO);
}
