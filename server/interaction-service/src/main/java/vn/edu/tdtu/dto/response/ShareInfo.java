package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.model.User;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareInfo implements Serializable {
    private String id;
    private String status;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date sharedAt;
    private User sharedUser;
    private EPrivacy privacy;
}
