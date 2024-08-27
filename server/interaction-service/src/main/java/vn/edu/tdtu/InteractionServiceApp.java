package vn.edu.tdtu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class InteractionServiceApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(InteractionServiceApp.class, args);
    }
}
