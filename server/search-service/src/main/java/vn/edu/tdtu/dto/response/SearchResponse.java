package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.dto.UserDTO;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchResponse implements Serializable {
    private List<UserDTO> users;
    private List<PostDTO> posts;
    private List<GroupDTO> groups;
}
