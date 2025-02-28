package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.PromoteToAdminRequest;

public interface GroupAdminService {
    public void adminCheck(String groupId);
    public ResDTO<?> promoteToAdmin(PromoteToAdminRequest request);
}
