package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.response.FriendRequestNoti;

@Service
@RequiredArgsConstructor
public class SendKafkaMsgService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void pushFriendRequestNoti(FriendRequestNoti notification){
        kafkaTemplate.send("friend-request", notification);
    }
}
