package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.Reaction;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.model.data.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewerResponse  {
    private String id;
    private LocalDateTime viewedAt;
    private User user;
    private List<Reaction> reactions;
}