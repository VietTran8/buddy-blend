package vn.edu.tdtu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MessageServiceApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(MessageServiceApp.class, args);
    }
}
