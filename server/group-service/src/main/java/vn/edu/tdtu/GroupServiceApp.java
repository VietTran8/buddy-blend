package vn.edu.tdtu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"vn.tdtu.common", "vn.edu.tdtu"})
@EnableFeignClients
public class GroupServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApp.class, args);
    }
}