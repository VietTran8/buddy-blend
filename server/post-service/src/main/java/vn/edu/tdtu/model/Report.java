package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Report {
    private String id;
    private String reason;
    private String userId;
    private String postId;
    private Date createAt;
}