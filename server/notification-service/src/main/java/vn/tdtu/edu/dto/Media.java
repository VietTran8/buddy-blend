package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.edu.enums.EFileType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Media implements Serializable {
    private String id;
    private String url;
    private EFileType type;
    private String ownerId;
    private String thumbnail;
}