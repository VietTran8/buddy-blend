package vn.edu.tdtu.service.interfaces;


import vn.tdtu.common.viewmodel.ResponseVM;

public interface BlockingService {
    ResponseVM<?> handleUserBlocking(String banUserId);

    ResponseVM<?> getBlockedUserList();
}
