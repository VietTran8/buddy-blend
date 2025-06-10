package vn.edu.tdtu.message;

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
    private List<String> broadcastIds;
    private PostDTO post;
}
