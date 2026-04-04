package com.example.service;

import com.example.model.ReviewRequest;
import com.example.model.ReviewResponse;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import java.util.List;

@Service
public class ReviewService {

    @Value("${openai.api.key}")
    private String apiKey;

    public ReviewResponse reviewCode(ReviewRequest request) {
        OpenAiService service = new OpenAiService(apiKey);

        String prompt = String.format(
            "Review this %s code. Return ONLY JSON like {\"message\":\"...\",\"score\":number}\n\nCode:\n%s",
            request.getLanguage(), request.getCode()
        );

        try {
            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(List.of(
                            new ChatMessage("system", "You are a code reviewer."),
                            new ChatMessage("user", prompt)
                    ))
                    .build();

            String reply = service.createChatCompletion(chatRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = mapper.readValue(reply, Map.class);

            return new ReviewResponse(
                    (String) json.get("message"),
                    (Integer) json.get("score")
            );

        } catch (Exception e) {
            return new ReviewResponse("Error: " + e.getMessage(), 0);
        }
    }
}