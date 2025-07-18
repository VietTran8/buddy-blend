package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.es.SyncGroup;
import vn.tdtu.common.dto.GroupDTO;

import java.util.List;

public interface GroupService {
    void saveGroup(SyncGroup group);

    void updateGroup(SyncGroup group);

    void deleteGroup(String groupId);

    List<GroupDTO> findByNameContaining(String key, String fuzziness);
}
