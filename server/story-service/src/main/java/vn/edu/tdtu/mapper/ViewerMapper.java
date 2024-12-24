package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.ViewerResponse;
import vn.edu.tdtu.model.Reaction;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.model.data.User;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ViewerMapper {
    public List<ViewerResponse> mapToDtos(List<Viewer> viewers, Map<String, User> userMap) {
        return viewers.stream().map(viewer -> {
            ViewerResponse viewerResponse = new ViewerResponse();

            viewerResponse.setId(viewer.getId());
            viewerResponse.setUser(userMap.get(viewer.getUserId()));
            viewerResponse.setReactions(viewer.getReactions()
                    .stream()
                    .sorted(Comparator.comparing(Reaction::getCreatedAt).reversed())
                    .limit(5)
                    .toList());
            viewerResponse.setViewedAt(viewer.getViewedAt());

            return viewerResponse;
        }).toList();
    }
}
