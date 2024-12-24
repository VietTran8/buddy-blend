package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFavourite {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private String id;
    @ManyToOne()
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User user;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ElementCollection
    private List<String> postIds;
}