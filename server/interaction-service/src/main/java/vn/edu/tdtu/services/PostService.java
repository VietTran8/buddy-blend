package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.repositories.httpclient.PostClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostClient postClient;

    public Post findById(String token, String postId){
        ResDTO<Post> response = postClient.findById(token, postId);

        return response.getData();
    }
}

