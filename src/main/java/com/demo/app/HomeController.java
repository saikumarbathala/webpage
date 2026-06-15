package com.demo.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    // Home page — HTML చూపిస్తుంది
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello from Spring Boot on EC2!");
        model.addAttribute("version", "1.0.0");
        return "index";  // templates/index.html render అవుతుంది
    }

    // Health check endpoint — Jenkins & load balancer కి పనికొస్తుంది
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }
}
