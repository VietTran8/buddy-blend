package vn.edu.tdtu.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.util.CustomLocalDateTimeDeserialize;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FetchNewsFeedReq {
    @JsonDeserialize(using = CustomLocalDateTimeDeserialize.class)
    LocalDateTime startTime;
    int page;
    int size;
}
