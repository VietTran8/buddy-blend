package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPrivacy;

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
    private MinimizedUserResponse sharedUser;
    private EPrivacy privacy;
}
