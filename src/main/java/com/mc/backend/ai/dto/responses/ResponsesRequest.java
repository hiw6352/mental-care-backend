package com.mc.backend.ai.dto.responses;

import com.mc.backend.ai.dto.responses.input.Message;
import java.util.List;

public record ResponsesRequest(
    String model,
    List<Message> input,
    TextOptions text,
    Integer max_output_tokens
) {

    public static ResponsesRequest of(String model, List<Message> input, ResponseFormat fmt,
        int maxTokens) {
        return new ResponsesRequest(model, input, new TextOptions(fmt), maxTokens);
    }
}