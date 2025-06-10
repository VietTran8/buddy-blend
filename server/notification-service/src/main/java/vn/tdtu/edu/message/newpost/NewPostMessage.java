package vn.tdtu.edu.message.newpost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.common.dto.PostDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewPostMessage {
    private PostDTO post;
    private List<String> broadcastIds;
}
