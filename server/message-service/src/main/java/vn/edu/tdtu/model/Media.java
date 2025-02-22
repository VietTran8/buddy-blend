package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.edu.tdtu.enums.EFileType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Media {
    private String url;
    private EFileType type;
    private String thumbnail;
}
