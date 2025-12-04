package com.example.planifest.controller.controllerview;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.dto.ChatMessage;
import com.example.planifest.dto.ChatResponse;
import com.example.planifest.service.ChatBotService;

@RestController
@RequestMapping("/bot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/message")
    public ChatResponse chat(@RequestBody ChatMessage message) {
        String reply = chatBotService.respond(message.getMessage());
        return new ChatResponse(reply);
    }
}
