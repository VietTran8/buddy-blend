package vn.edu.tdtu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.MessageNoti;

@Service
@RequiredArgsConstructor
public class SendKafkaMsgService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void publishMessageNoti(MessageNoti message){
        kafkaTemplate.send("chatting", message);
    }
}