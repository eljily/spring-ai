package com.sidibrahim.springai.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class OpenAiRestController {
   private final ChatClient chatClient;
   @Value("${spring.ai.openai.api-key}")
   private String apiKey;

    public OpenAiRestController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "message") String message){
        return chatClient.call(message);
    }

    @GetMapping("/movies")
    public Map movies(@RequestParam(name = "category",defaultValue = "action") String category
            , @RequestParam(name = "year",defaultValue = "2019") int year) throws JsonProcessingException {
        OpenAiApi openAiApi = new OpenAiApi(apiKey);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel("gpt-3.5-turbo")
                .withTemperature(0f)
                .withMaxTokens(1000)
                .build();
        OpenAiChatClient openAiChatClient = new OpenAiChatClient(openAiApi,options);
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(
                """
                   I need you to give me the best movie on the given category
                   : {category} on the given year:{year}.
                   The output should be in json format including the following fields 
                   :
                   -category<The given category>
                   -year<The given Year>
                   -title <the title of the movie>
                   -producer <the producer of the movie>
                   -actors <A list of main actors of the movie>
                   -summary <a very small summary of the movie>"""
        );
        Prompt prompt = promptTemplate.create(Map.of("category",category
                ,"year",year));
        String content = openAiChatClient
                .call(prompt)
                .getResult()
                .getOutput()
                .getContent();
        return new ObjectMapper().readValue(content, Map.class);
    }

}
