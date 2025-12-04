package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatBotController {

    @GetMapping("/chat")
    public String showChat() {
        // Spring busca automáticamente: src/main/resources/templates/chat.html
        return "chat";
    }
}
