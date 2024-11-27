package com.beb.backend;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class DemoController {

    @GetMapping("/demo")
    public String demo(){
        return "hi-jenkins-test444444444";
    }
}
