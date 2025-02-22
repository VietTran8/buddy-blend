package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EFileType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Media implements Serializable {
    private String id;
    private String url;
    private EFileType type;
    private String ownerId;
    private String thumbnail;
}
