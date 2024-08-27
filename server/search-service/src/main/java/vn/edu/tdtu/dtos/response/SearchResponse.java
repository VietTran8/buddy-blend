package vn.edu.tdtu.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.User;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchResponse implements Serializable {
    private List<User> users;
    private List<Post> posts;
}
