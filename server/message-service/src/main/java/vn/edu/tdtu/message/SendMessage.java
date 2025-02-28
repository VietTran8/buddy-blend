package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.Media;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMessage {
    private String content;
    private List<Media> medias;
    private String toUserId;
}
