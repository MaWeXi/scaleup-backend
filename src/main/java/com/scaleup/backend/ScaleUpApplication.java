package com.scaleup.backend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Paths;


@SpringBootApplication
public class ScaleUpApplication {

    public static void main(String[] args) {
        String OS = System.getProperty("os.name").toLowerCase();

        if (OS.contains("win")) {
            System.setProperty("hadoop.home.dir", Paths.get("winutils").toAbsolutePath().toString());
            // System.setProperty("hadoop.home.dir", "C:/Winutils/hadoop-3.0.0/");
        } else {
            System.setProperty("hadoop.home.dir", "/");
        }
        SpringApplication.run(ScaleUpApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
