package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.ReportResponse;
import vn.edu.tdtu.dto.response.ReportUserResponse;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.Report;
import vn.edu.tdtu.service.impl.UserServiceImpl;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.dto.UserDTO;

@Component
@RequiredArgsConstructor
public class ReportResponseMapper {
    private final PostService postService;
    private final UserServiceImpl userService;

    public ReportResponse mapToDto(Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setReason(report.getReason());
        response.setCreateAt(report.getCreateAt());

        UserDTO foundUser = userService.findById(report.getUserId());
        if (foundUser != null) {
            ReportUserResponse reportUserResponse = new ReportUserResponse();
            reportUserResponse.setId(foundUser.getId());
            reportUserResponse.setFullName(foundUser.getUserFullName());
            reportUserResponse.setProfilePicture(foundUser.getProfilePicture());

            response.setReportedBy(reportUserResponse);
        }

        Post foundPost = postService.findPostById(report.getPostId());
        if (foundPost != null) {
            PostDTO postResponse = postService.mapToPostDTO(foundPost);
            response.setPost(postResponse);
        }

        return response;
    }
}
