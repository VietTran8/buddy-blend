package vn.edu.tdtu.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.edu.tdtu.message.ModerateImagesMessage;
import vn.edu.tdtu.message.ModerateImagesResultsMessage;
import vn.edu.tdtu.producer.KafkaEventPublisher;
import vn.edu.tdtu.service.interfaces.ModerationService;

@Component
@RequiredArgsConstructor
public class KafkaEventConsumer {
    private final ModerationService moderationService;
    private final KafkaEventPublisher publisher;

    @KafkaListener(groupId = "ModerateGroup", topics = "${kafka.topic.moderate.name}")
    public void consumeModerateTopic(ModerateImagesMessage message){
        ModerateResponseDto result = moderationService.bulkModerateImages(message.getImageUrls());

        publisher.publishModerationResult(new ModerateImagesResultsMessage(result, message.getPostId()));
    }
}
