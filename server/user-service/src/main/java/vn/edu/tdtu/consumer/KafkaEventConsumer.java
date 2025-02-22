package vn.edu.tdtu.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.constant.RedisKey;
import vn.edu.tdtu.message.UserConnectMessage;
import vn.edu.tdtu.model.redis.UserStatus;
import vn.edu.tdtu.service.interfaces.RedisService;

@Component
@RequiredArgsConstructor
public class KafkaEventConsumer {
    private final RedisService<String> redisService;

    @KafkaListener(topics = "${kafka.topic.user-connected.name}", groupId = "userConnectedGroup")
    public void consumeUserConnectedTopic(UserConnectMessage message) {
        redisService.addToSet(RedisKey.combineKey(
                RedisKey.USER_STATUS_KEY,
                message.getUserId()
        ), message.getSessionId());
    }

    @KafkaListener(topics = "${kafka.topic.user-disconnected.name}", groupId = "userDisconnectedGroup")
    public void consumeUserDisconnectedTopic(UserConnectMessage message) {
        redisService.removeFromSet(
                RedisKey.combineKey(RedisKey.USER_STATUS_KEY, message.getUserId()),
                message.getSessionId()
        );
    }
}
