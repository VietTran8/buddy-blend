package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.repository.httpclient.PostClient;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.tdtu.common.dto.PostDTO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostClient postClient;

    @Override
    public List<PostDTO> findByIds(String token, FindByIdsReq reqDTO) {
        return postClient.findAll(token, reqDTO).getData();
    }
}
