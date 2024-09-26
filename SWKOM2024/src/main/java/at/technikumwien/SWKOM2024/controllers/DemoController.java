package at.technikumwien.SWKOM2024.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello again World!";
    }

    @GetMapping("/members")
    public String getMembers(){
        return "Drian, Juled, Alfredo, Bruda";
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
