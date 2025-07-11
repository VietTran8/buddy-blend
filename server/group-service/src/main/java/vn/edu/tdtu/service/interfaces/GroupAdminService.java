package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.PromoteToAdminRequest;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface GroupAdminService {
    void adminCheck(String groupId);

    ResponseVM<?> promoteToAdmin(PromoteToAdminRequest request);
}
