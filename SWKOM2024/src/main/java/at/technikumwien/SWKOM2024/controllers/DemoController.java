package at.technikumwien.SWKOM2024.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello World!";
    }

    @GetMapping("/members")
    public String getMembers(){
        return "Drian, Juled, Alfredo";
    }

    @GetMapping("/status")
    public String getStatus(){
        return "Online";
    }

    @GetMapping("/version")
    public String getVersion(){
        return "v1";
    }
}
