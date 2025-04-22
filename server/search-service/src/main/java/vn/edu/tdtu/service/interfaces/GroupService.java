package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.Group;
import vn.edu.tdtu.model.es.SyncGroup;

import java.util.List;

public interface GroupService {
    void saveGroup(SyncGroup group);

    void updateGroup(SyncGroup group);

    void deleteGroup(String groupId);

    List<Group> findByNameContaining(String tokenHeader, String key, String fuzziness);
}
