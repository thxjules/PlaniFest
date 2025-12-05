package com.example.planifest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @GetMapping
    public String loadChatPage() {
        return "chat";
    }

    @PostMapping("/message")
    @ResponseBody
    public ChatResponse chatResponse(@RequestBody ChatMessage body) {
        String reply = chatBotService.respond(body.getMessage());
        return new ChatResponse(reply);
    }
}