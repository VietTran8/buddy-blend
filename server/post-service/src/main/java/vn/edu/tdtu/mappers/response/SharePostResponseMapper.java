package vn.edu.tdtu.mappers.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.PostResponse;
import vn.edu.tdtu.dtos.response.ShareInfo;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.models.PostShare;
import vn.edu.tdtu.repositories.PostRepository;
import vn.edu.tdtu.services.UserService;
import vn.edu.tdtu.utils.DateUtils;
import vn.edu.tdtu.utils.JwtUtils;

@Component
@RequiredArgsConstructor
public class SharePostResponseMapper {
    private final PostRepository postRepository;
    private final PostResponseMapper postResponseMapper;
    private final JwtUtils jwtUtils;
    private final UserService userService;

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
