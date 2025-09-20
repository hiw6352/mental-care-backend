package com.mc.backend.ai.dto;

public record ResponsesRequest(
    String model,
    Object input,
    TextOptions text,
    Integer max_output_tokens
) {

    public static ResponsesRequest of(String model, Object input, ResponseFormat fmt,
        int maxTokens) {
        return new ResponsesRequest(model, input, new TextOptions(fmt), maxTokens);
    }
}