package vn.edu.tdtu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ModerationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ModerationServiceApp.class, args);
    }
}