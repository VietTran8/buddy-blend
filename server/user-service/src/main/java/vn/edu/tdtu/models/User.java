package vn.edu.tdtu.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EUserRole;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    @JsonIgnore
    private String normalizedName;
    private String gender;
    private String phone;
    private String fromCity;
    private String profilePicture;
    private String cover;
    private String bio;
    private String notificationKey;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    @JsonIgnore
    private boolean active;
    @JsonIgnore
    private EUserRole role;
    @JsonIgnore
    @OneToMany(mappedBy = "fromUser")
    private List<FriendRequest> myFriendRequests;
    @OneToMany(mappedBy = "toUser")
    @JsonIgnore
    private List<FriendRequest> myRequests;
    public String getUserFullName(){
        return String.join(" ", this.getFirstName(), this.getMiddleName(), this.getLastName());
    }
}