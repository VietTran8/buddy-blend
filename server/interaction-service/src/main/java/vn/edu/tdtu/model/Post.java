package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.dto.response.ShareInfo;
import vn.edu.tdtu.enums.EPostType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post implements Serializable {
    private String id;
    private String content;
    private User user;
    private EPostType type;
    private ShareInfo shareInfo;
}