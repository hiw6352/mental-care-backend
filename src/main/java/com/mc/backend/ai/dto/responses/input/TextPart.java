package com.mc.backend.ai.dto.responses.input;

public record TextPart(String type, String text) {

    public static TextPart input(String text) {
        return new TextPart("input_text", text);
    }

    public static TextPart output(String text) {
        return new TextPart("output_text", text);
    }

    public static TextPart refusal(String text) {
        return new TextPart("refusal", text);
    } // 필요 시
}
