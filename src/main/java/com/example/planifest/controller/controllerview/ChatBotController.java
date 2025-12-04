package com.example.planifest.controller.controllerview;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/chatbot")
public class ChatBotController {

    @GetMapping
    public String showChat() {
        return "chat"; // carga chat.html
    }

    @PostMapping("/message")
    @ResponseBody
    public Map<String, String> chatResponse(@RequestBody Map<String, String> body) {

        String userText = body.get("message");

        String botReply = "Procesé tu mensaje: " + userText;

        Map<String, String> res = new HashMap<>();
        res.put("reply", botReply);

        return res;
    }
}