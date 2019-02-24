package com.venus.Renju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebGame {

    /**
     * start web game
     * @param args 
     */
    public void start(String[] args) {
        SpringApplication.run(WebGame.class, args);
    }

}
