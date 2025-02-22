package vn.edu.tdtu.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.dto.response.TopReacts;
import vn.edu.tdtu.enums.EReactionType;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {
    private String id;
    private String content;
    private String parentId;
    private List<String> imageUrls;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;
    private User user;
    private List<TopReacts> topReacts;
    private int noReactions;
    private EReactionType reacted;
    private boolean isMine;
    private List<Comment> children;
}