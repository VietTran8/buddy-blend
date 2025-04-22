package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;

public interface BlockingService {
    ResDTO<?> handleUserBlocking(String banUserId);

    ResDTO<?> getBlockedUserList();
}
