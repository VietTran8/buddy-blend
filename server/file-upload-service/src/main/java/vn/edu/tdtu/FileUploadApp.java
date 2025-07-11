package vn.edu.tdtu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"vn.tdtu.common", "vn.edu.tdtu"})
public class FileUploadApp {
    public static void main(String[] args) {
        SpringApplication.run(FileUploadApp.class, args);
    }
}
