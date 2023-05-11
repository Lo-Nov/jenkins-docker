package io.eclectics.cicd.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Api {
    @GetMapping("/test")
    public String message(){
        return "welcome to my first jenkins cicd test";
    }
}
