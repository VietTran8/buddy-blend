package vn.edu.tdtu.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeenMessage {
    private String fromUserId;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date seenTime;
}
