package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FcmResponse {

    @JsonProperty("notification_key")
    private String notificationKey;
}