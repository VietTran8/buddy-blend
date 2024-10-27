package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private boolean isAdmin;
    private boolean isPending;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private LocalDateTime joinedAt;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "groupId")
    private Group group;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "memberId")
    private Member member;
}
