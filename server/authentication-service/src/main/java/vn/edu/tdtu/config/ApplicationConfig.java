package vn.edu.tdtu.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.tdtu.common.utils.JwtUtils;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    @Value("${kafka.topic.send-otp-mail.name}")
    private String sendOtpMailTopicName;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public NewTopic sendMailTopic() {
        return new NewTopic(sendOtpMailTopicName, 2, (short) 1);
    }

}
