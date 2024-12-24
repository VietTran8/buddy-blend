package vn.edu.tdtu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.message.ModerateImagesResultsMessage;
import vn.edu.tdtu.message.ModerationNotificationMsg;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.service.intefaces.FileService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    private final PostRepository postRepository;
    private final KafkaEventPublisher publisher;
    private final FileService fileService;

    @KafkaListener(groupId = "ModerateResultGroup", topics = "${kafka.topic.moderation-result.name}")
    public void consumeModerationResult(ModerateImagesResultsMessage message) {
        if(!message.isAccept()) {
            Optional<Post> optionalPost = postRepository.findById(message.getPostId());

            optionalPost.ifPresent((post) -> {
                post.getImageUrls().forEach(image -> fileService.delete(image, EFileType.TYPE_IMG));
                postRepository.delete(post);

                publisher.pubModerateResultNotificationMessage(new ModerationNotificationMsg(message, post.getUserId()));

                log.info(String.format("Kafka consumer: Post [%s] is deleted", post.getId()));
            });
        }
    }
}
