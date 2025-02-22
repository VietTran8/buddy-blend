package vn.tdtu.edu.message.newpost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewPostMessage {
    private PostMessage post;
    private List<String> broadcastIds;
}
