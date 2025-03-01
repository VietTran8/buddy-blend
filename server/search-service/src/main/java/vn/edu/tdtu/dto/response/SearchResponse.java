package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.data.Group;
import vn.edu.tdtu.model.data.Post;
import vn.edu.tdtu.model.data.User;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchResponse implements Serializable {
    private List<User> users;
    private List<Post> posts;
    private List<Group> groups;
}
