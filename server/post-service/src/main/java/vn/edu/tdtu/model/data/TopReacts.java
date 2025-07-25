package vn.edu.tdtu.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopReacts implements Serializable {
    private EReactionType type;
    private int count;
}
