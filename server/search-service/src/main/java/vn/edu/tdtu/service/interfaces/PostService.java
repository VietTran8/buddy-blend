package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.es.SyncPost;
import vn.tdtu.common.dto.PostDTO;

import java.util.List;

public interface PostService {
    void savePost(SyncPost post);

    void updatePost(SyncPost post);

    void deletePost(SyncPost post);

    List<PostDTO> findByContentContaining(String key, String fuzziness);
}
