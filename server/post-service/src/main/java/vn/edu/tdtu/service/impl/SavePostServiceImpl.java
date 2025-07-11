package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.response.SavePostResponse;
import vn.edu.tdtu.model.SavePost;
import vn.edu.tdtu.repository.SavePostRepository;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.edu.tdtu.service.intefaces.SavePostService;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavePostServiceImpl implements SavePostService {
    private final SavePostRepository savePostRepository;
    private final PostService postService;

    @Override
    public ResponseVM<SavePostResponse> handleSavePost(String postId) {
        String userId = SecurityContextUtils.getUserId();

        SavePostResponse responseData = new SavePostResponse();
        responseData.setSavedId(postId);

        ResponseVM<SavePostResponse> response = new ResponseVM<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Post.POST_SAVE_POST);
        response.setData(responseData);

        Optional<SavePost> optionalSavePost = savePostRepository.findByUserId(userId);

        SavePost savePost = optionalSavePost.orElseGet(() -> {
            SavePost newSavePost = new SavePost();
            newSavePost.setPostIds(new ArrayList<>());
            newSavePost.setUserId(userId);

            return newSavePost;
        });

        if (savePost.getPostIds().contains(postId)) {
            savePost.getPostIds().remove(postId);
            response.setMessage(MessageCode.Post.POST_UNSAVE_POST);
            responseData.setSavedId(null);
        } else
            savePost.getPostIds().add(postId);

        savePostRepository.save(savePost);

        return response;
    }

    @Override
    public ResponseVM<List<PostDTO>> getUserSavedPost(String token) {
        ResponseVM<List<PostDTO>> response = new ResponseVM<>();
        response.setMessage(MessageCode.Post.POST_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(new ArrayList<>());

        String userId = SecurityContextUtils.getUserId();
        SavePost foundSavePost = savePostRepository.findByUserId(userId).orElse(null);

        if (foundSavePost == null) {
            return response;
        }

        List<PostDTO> responseData = postService.findPostRespByIds(token, new FindByIdsReq(foundSavePost.getPostIds())).getData();

        response.setData(responseData);

        return response;
    }
}
