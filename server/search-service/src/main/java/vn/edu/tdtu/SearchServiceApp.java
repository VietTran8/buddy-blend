package vn.edu.tdtu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"vn.tdtu.common", "vn.edu.tdtu"})
@EnableCaching
@EnableFeignClients
public class SearchServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApp.class, args);
    }
}
