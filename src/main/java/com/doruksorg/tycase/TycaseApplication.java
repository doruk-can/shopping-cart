package com.doruksorg.tycase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.ZoneOffset;
import java.util.TimeZone;

@EnableMongoAuditing
@SpringBootApplication
public class TycaseApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        SpringApplication.run(TycaseApplication.class, args);
    }

}
