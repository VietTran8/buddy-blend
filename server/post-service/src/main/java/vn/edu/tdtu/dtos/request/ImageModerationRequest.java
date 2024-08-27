package vn.edu.tdtu.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageModerationRequest {
    private String media;
    private String api_user;
    private String api_secret;
    private String workflow;
}
