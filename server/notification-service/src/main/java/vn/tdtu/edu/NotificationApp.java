package vn.tdtu.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"vn.tdtu.common", "vn.tdtu.edu"})
public class NotificationApp {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApp.class, args);
    }
}
