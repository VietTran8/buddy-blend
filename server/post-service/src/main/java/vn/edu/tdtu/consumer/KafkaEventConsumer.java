package vn.edu.tdtu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.message.ModerateResultsMessage;
import vn.edu.tdtu.message.ModerationNotificationMsg;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    private final PostRepository postRepository;
    private final KafkaEventPublisher publisher;
    private final MediaRepository mediaRepository;

    @KafkaListener(groupId = "ModerateResultGroup", topics = "${kafka.topic.moderation-result.name}")
    public void consumeModerationResult(ModerateResultsMessage message) {
        if(!message.isAccept()) {
            Optional<Post> optionalPost = postRepository.findById(message.getRefId());

            optionalPost.ifPresent((post) -> {
                post.setDetached(true);

                List<Media> postMedias = mediaRepository.findAllById(post.getMediaIds());

                mediaRepository.saveAll(postMedias
                        .stream()
                        .peek(media -> media.setDetached(true))
                        .toList()
                );
                postRepository.save(post);

                publisher.pubModerateResultNotificationMessage(new ModerationNotificationMsg(message, post.getUserId()));

                log.info(String.format("Kafka consumer: Post [%s] is deleted", post.getId()));
            });
        }
    }
}
