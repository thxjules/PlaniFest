package com.example.planifest.controller.controllerview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.planifest.dto.ChatMessage;
import com.example.planifest.dto.ChatResponse;
import com.example.planifest.service.ChatBotService;

@Controller
@RequestMapping("/chatbot")
@CrossOrigin(origins = "*") // Solo para desarrollo, permite que fetch funcione
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @GetMapping
    public String showChat() {
        return "chat"; // Thymeleaf buscará chat.html en templates
    }

    @PostMapping("/message")
    @ResponseBody
    public ChatResponse chatResponse(@RequestBody ChatMessage body) {
        String userText = body.getMessage();
        String reply = chatBotService.respond(userText);
        return new ChatResponse(reply);
    }
}
