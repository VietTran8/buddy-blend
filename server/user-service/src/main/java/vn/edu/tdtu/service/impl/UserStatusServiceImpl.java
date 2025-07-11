package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.service.interfaces.RedisService;
import vn.edu.tdtu.service.interfaces.UserStatusService;
import vn.tdtu.common.utils.Constants;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {
    private final RedisService<String> redisService;

    @Override
    public boolean isUserOnline(String userId) {
        return redisService.getSetSize(Constants.RedisKey.combineKey(Constants.RedisKey.USER_STATUS_KEY, userId)) > 0;
    }
}
