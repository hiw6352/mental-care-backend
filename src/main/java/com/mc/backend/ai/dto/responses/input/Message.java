package com.mc.backend.ai.dto.responses.input;

import com.mc.backend.ai.enums.ChatRole;
import java.util.List;

public record Message(ChatRole role, List<TextPart> content) {

    public static Message system(String text) {
        return new Message(ChatRole.SYSTEM, List.of(TextPart.input(text)));
    }

    public static Message user(String text) {
        return new Message(ChatRole.USER, List.of(TextPart.input(text)));
    }

    // 과거 assistant 발화를 히스토리에 넣을 때는 output_text로!
    public static Message assistant(String text) {
        return new Message(ChatRole.ASSISTANT, List.of(TextPart.output(text)));
    }
}