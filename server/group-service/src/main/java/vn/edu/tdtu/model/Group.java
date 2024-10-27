package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EGroupPrivacy;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "UserGroup")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private EGroupPrivacy privacy;
    @Column
    private String description;
    private String avatar;
    private String cover;
    @JsonIgnore
    private boolean isDeleted;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private LocalDateTime createdAt;
    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    private List<GroupMember> groupMembers;
    @JsonIgnore
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Contribution> contributions;
}