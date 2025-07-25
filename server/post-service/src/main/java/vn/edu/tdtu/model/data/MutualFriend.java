package vn.edu.tdtu.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutualFriend implements Serializable {
    private String id;
    private String fullName;
    private String profileImage;
}