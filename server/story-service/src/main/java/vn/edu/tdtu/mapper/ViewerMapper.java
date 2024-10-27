package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.ViewerResponse;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ViewerMapper {
    public List<ViewerResponse> mapToDtos(List<Viewer> viewers, List<User> users) {
        Map<String, User> userMap = users.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        return viewers.stream().map(viewer -> {
            ViewerResponse viewerResponse = new ViewerResponse();

            viewerResponse.setId(viewer.getId());
            viewerResponse.setUser(userMap.get(viewer.getUserId()));
            viewerResponse.setReactions(viewer.getReactions());
            viewerResponse.setViewedAt(viewer.getViewedAt());

            return viewerResponse;
        }).toList();
    }
}
