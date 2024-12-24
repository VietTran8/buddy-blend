package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;

public interface BanningService {
    ResDTO<?> handleUserBanning(String banUserId);
    ResDTO<?> getBannedUserList();
}
