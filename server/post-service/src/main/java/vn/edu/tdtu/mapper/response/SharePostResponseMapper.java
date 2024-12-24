package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.dto.response.ShareInfo;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.PostShare;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.service.impl.UserServiceImpl;
import vn.edu.tdtu.util.DateUtils;
import vn.edu.tdtu.util.JwtUtils;

@Component
@RequiredArgsConstructor
public class SharePostResponseMapper {
    private final PostRepository postRepository;
    private final PostResponseMapper postResponseMapper;
    private final JwtUtils jwtUtils;
    private final UserServiceImpl userService;

    public PostResponse mapToPostResponse(String token, PostShare postShare){
        String userId = jwtUtils.getUserIdFromJwtToken(token);

        Post foundPost = postRepository.findById(postShare.getSharedPostId()).orElse(null);
        if(foundPost != null){
            PostResponse postResponse = postResponseMapper.mapToDto(token, postShare.getId(), foundPost, false);
            postResponse.setMine(foundPost.getUserId().equals(userId));
            postResponse.setType(EPostType.SHARE);

            ShareInfo shareInfo = new ShareInfo();
            shareInfo.setStatus(postShare.getStatus());
            shareInfo.setSharedUser(userService.findById(token, postShare.getSharedUserId()));
            shareInfo.setSharedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));
            shareInfo.setId(postShare.getId());
            shareInfo.setPrivacy(postShare.getPrivacy());

            postResponse.setShareInfo(shareInfo);
            postResponse.setCreatedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));

            return postResponse;
        }
        return null;
    }
}
