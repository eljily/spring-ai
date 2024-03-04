package com.sidibrahim.springai.web;

import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OpenAiRestController {
   private final ChatClient chatClient;

    public OpenAiRestController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "message") String message){
        return chatClient.call(message);
    }

}
