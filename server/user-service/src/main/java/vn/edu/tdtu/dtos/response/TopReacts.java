package vn.edu.tdtu.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EReactionType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopReacts implements Serializable {
    private EReactionType type;
    private int count;
}