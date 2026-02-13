package haru.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PharmacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmacyApplication.class, args);
        System.out.println("Pharmacy is running! Don't forget your homework!");
    }
}
