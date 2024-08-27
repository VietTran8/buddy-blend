package vn.edu.tdtu.dtos.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.utils.CustomLocalDateTimeDeserialize;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FetchNewsFeedReq {
    @JsonDeserialize(using = CustomLocalDateTimeDeserialize.class)
    LocalDateTime startTime;
    int page;
    int size;
}
