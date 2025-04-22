package vn.edu.tdtu.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.edu.tdtu.message.ModerateMessage;
import vn.edu.tdtu.message.ModerateResultsMessage;
import vn.edu.tdtu.producer.KafkaEventPublisher;
import vn.edu.tdtu.service.interfaces.ModerationService;

@Component
@RequiredArgsConstructor
public class KafkaEventConsumer {
    private final ModerationService moderationService;
    private final KafkaEventPublisher publisher;

    @KafkaListener(groupId = "ModerateGroup", topics = "${kafka.topic.moderate.name}")
    public void consumeModerateTopic(ModerateMessage message) {
        ModerateResponseDto result = moderationService.moderate(message);

        if (!result.isAccept())
            publisher.publishModerationResult(new ModerateResultsMessage(result, message));
    }
}
