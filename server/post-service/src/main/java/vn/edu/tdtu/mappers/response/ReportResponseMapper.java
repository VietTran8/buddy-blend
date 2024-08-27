package vn.edu.tdtu.mappers.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.PostResponse;
import vn.edu.tdtu.dtos.response.ReportResponse;
import vn.edu.tdtu.dtos.response.ReportUserResponse;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.models.Report;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.services.PostService;
import vn.edu.tdtu.services.UserService;

@Component
@RequiredArgsConstructor
public class ReportResponseMapper {
    private final PostService postService;
    private final UserService userService;

    public ReportResponse mapToDto(String token, Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setReason(report.getReason());
        response.setCreateAt(report.getCreateAt());

        User foundUser = userService.findById(token, report.getUserId());
        if(foundUser != null) {
            ReportUserResponse reportUserResponse = new ReportUserResponse();
            reportUserResponse.setId(foundUser.getId());
            reportUserResponse.setFullName(foundUser.getUserFullName());
            reportUserResponse.setProfilePicture(foundUser.getProfilePicture());

            response.setReportedBy(reportUserResponse);
        }

        Post foundPost = postService.findPostById(report.getPostId());
        if(foundPost != null) {
            PostResponse postResponse = postService.mapToPostResponse(token, foundPost);
            response.setPost(postResponse);
        }

        return response;
    }
}
