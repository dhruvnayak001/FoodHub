package com.foodorder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // Use Controller instead of RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";  // Spring will serve the static file
    }
}