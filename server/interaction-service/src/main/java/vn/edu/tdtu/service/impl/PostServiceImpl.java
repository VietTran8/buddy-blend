package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.repository.httpclient.PostClient;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.tdtu.common.dto.PostDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostClient postClient;

    @Override
    public PostDTO findById(String token, String postId) {
        ResDTO<PostDTO> response = postClient.findById(token, postId);

        return response.getData();
    }
}

