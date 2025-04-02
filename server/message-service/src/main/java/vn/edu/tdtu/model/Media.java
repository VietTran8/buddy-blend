package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EFileType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Media {
    private String url;
    private EFileType type;
    private String thumbnail;
}
