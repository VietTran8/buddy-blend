package vn.edu.tdtu.mapper.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.request.SaveUserReqDTO;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.utils.StringUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SaveUserReqMapper {
    public User mapToObject(SaveUserReqDTO dto){
        User user = new User();

        user.setActive(true);
        user.setRole(EUserRole.ROLE_USER);
        user.setBio(dto.getBio());
        user.setGender(dto.getGender());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setProfilePicture(dto.getProfilePicture());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setNormalizedName(StringUtils.toSlug(user.getFirstName().concat(user.getMiddleName()).concat(user.getLastName())));

        return user;
    }
}
