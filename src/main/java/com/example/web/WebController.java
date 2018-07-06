package com.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class WebController {

    @GetMapping("/greeting")
    public String greeting() {
        return "greeting";
    }

    @RequestMapping("/")
    public String defaultPage(ModelMap map) {
        map.addAttribute("time", LocalDateTime.now().toString());
        return "user-page";
    }

    @RequestMapping("/user-page")
    public String userPage(ModelMap map) {
        map.addAttribute("time", LocalDateTime.now().toString());
        return "user-page";
    }
}
