package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.dto.response.PostResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewPostMessage {
    private List<String> broadcastIds;
    private PostResponse post;
}
