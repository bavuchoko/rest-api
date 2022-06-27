package com.pjs.studyrestapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudyRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRestApiApplication.class, args);
    }

    public StudyRestApiApplication() {
    }
}
