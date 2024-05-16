package ru.artemiyandarina.blps_lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("ru.artemiyandarina.blps_lab2.config.properties")
public class BlpsLab2Application {

    public static void main(String[] args) {
        SpringApplication.run(BlpsLab2Application.class, args);
    }

}
