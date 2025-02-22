package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.RedisKey;
import vn.edu.tdtu.service.interfaces.RedisService;
import vn.edu.tdtu.service.interfaces.UserStatusService;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {
    private final RedisService<String> redisService;
    @Override
    public boolean isUserOnline(String userId) {
        return redisService.getSetSize(RedisKey.combineKey(RedisKey.USER_STATUS_KEY, userId)) > 0;
    }
}
