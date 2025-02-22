package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.edu.enums.EReactionType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopReacts implements Serializable {
    private EReactionType type;
    private int count;
}
