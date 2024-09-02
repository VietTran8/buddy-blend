package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.request.FindByIdsReq;
import vn.edu.tdtu.dtos.response.PostResponse;
import vn.edu.tdtu.repositories.httpclient.PostClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostClient postClient;

    public List<PostResponse> findByIds(String token, FindByIdsReq reqDTO) {
        return postClient.findAll(token, reqDTO).getData();
    }
}
