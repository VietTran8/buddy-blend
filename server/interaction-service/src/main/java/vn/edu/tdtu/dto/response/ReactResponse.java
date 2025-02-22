package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.model.data.User;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactResponse {
    private String id;
    private EReactionType type;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;
    private User user;
    private boolean isMine;
}
