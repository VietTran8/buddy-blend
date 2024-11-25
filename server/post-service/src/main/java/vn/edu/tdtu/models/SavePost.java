package vn.edu.tdtu.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SavePost {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private List<String> postIds;
}
