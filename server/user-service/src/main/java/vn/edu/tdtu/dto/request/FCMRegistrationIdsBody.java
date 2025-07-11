package vn.edu.tdtu.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FCMRegistrationIdsBody {

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("notification_key_name")
    private String notificationKeyName;

    @JsonProperty("registration_ids")
    private List<String> registrationIds;

    @JsonProperty("notification_key")
    private String notificationKey;

}
