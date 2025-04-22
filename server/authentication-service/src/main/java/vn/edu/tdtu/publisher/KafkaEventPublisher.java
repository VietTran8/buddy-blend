package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.message.SendOTPMailMessage;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.send-otp-mail.name}")
    private String sendOtpMailTopicName;

    public void publishSendOtpMailMsg(SendOTPMailMessage message) {
        kafkaTemplate.send(sendOtpMailTopicName, message);
    }
}
