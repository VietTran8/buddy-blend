package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.edu.tdtu.enums.EPrivacy;

import java.time.LocalDateTime;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class PostShare {
    @Id
    private String id;
    private String status;
    private EPrivacy privacy;
    private LocalDateTime sharedAt;
    private String sharedUserId;
    private String sharedPostId;
    @JsonIgnore
    private String normalizedStatus;
}