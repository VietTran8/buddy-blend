package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.tdtu.common.enums.post.EFileType;

import java.io.Serializable;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Media implements Serializable {
    @Id
    private String id;
    private String url;
    private EFileType type;
    private String ownerId;
    private String thumbnail;
    @JsonIgnore
    private boolean detached;
}