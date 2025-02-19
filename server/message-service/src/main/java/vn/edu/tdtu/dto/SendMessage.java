package vn.edu.tdtu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMessage {
    private String content;
    private List<String> imageUrls;
    private String toUserId;
}
