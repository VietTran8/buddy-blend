package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.dto.response.SavePostResponse;
import vn.edu.tdtu.model.SavePost;
import vn.edu.tdtu.repository.SavePostRepository;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.edu.tdtu.service.intefaces.SavePostService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavePostServiceImpl implements SavePostService {
    private final SavePostRepository savePostRepository;
    private final PostService postService;

    @Override
    public ResDTO<SavePostResponse> handleSavePost(String postId) {
        String userId = SecurityContextUtils.getUserId();

        SavePostResponse responseData = new SavePostResponse();
        responseData.setSavedId(postId);

        ResDTO<SavePostResponse> response = new ResDTO<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Đã lưu bài viết");
        response.setData(responseData);

        Optional<SavePost> optionalSavePost = savePostRepository.findByUserId(userId);

        SavePost savePost = optionalSavePost.orElseGet(() -> {
            SavePost newSavePost = new SavePost();
            newSavePost.setPostIds(new ArrayList<>());
            newSavePost.setUserId(userId);

            return newSavePost;
        });

        if(savePost.getPostIds().contains(postId)) {
            savePost.getPostIds().remove(postId);
            response.setMessage("Đã bỏ lưu bài viết");
            responseData.setSavedId(null);
        }
        else
            savePost.getPostIds().add(postId);

        savePostRepository.save(savePost);

        return response;
    }

    @Override
    public ResDTO<List<PostResponse>> getUserSavedPost(String token){
        ResDTO<List<PostResponse>> response = new ResDTO<>();
        response.setMessage("Posts fetched successfully");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(new ArrayList<>());

        String userId = SecurityContextUtils.getUserId();
        SavePost foundSavePost = savePostRepository.findByUserId(userId).orElse(null);

        if(foundSavePost == null) {
            return response;
        }

        List<PostResponse> responseData = postService.findPostRespByIds(token, new FindByIdsReq(foundSavePost.getPostIds())).getData();

        response.setData(responseData);

        return response;
    }
}
