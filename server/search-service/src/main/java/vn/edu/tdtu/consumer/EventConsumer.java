package vn.edu.tdtu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.message.SyncPostMsg;
import vn.edu.tdtu.message.SyncUserMsg;
import vn.edu.tdtu.service.PostService;
import vn.edu.tdtu.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final UserService userService;
    private final PostService postService;


    @KafkaListener(groupId = "SyncUserData", topics = "${kafka.topic.sync-user.name}")
    public void syncUserData(SyncUserMsg msg) {

        log.info(msg.toString());

        switch (msg.getSyncType()) {
            case TYPE_CREATE -> {
                userService.saveUser(msg.getUser());
            }
            case TYPE_DELETE -> {
                userService.deleteUser(msg.getUser());
            }
            case TYPE_UPDATE -> {
                userService.updateUser(msg.getUser());
            }
        }
    }

    @KafkaListener(groupId = "SyncPostData", topics = "${kafka.topic.sync-post.name}")
    public void syncPostData(SyncPostMsg msg) {

        log.info(msg.toString());

        switch (msg.getSyncType()) {
            case TYPE_CREATE -> {
                postService.savePost(msg.getPost());
            }
            case TYPE_DELETE -> {
                postService.deletePost(msg.getPost());
            }
            case TYPE_UPDATE -> {
                postService.updatePost(msg.getPost());
            }
        }
    }
}
