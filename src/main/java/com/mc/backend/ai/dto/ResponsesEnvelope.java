package com.mc.backend.ai.dto;

import java.util.List;

public record ResponsesEnvelope(List<Output> output) {

    public String firstText() {
        if (output == null) {
            return null;
        }
        for (var o : output) {
            if (o.content() == null) {
                continue;
            }
            for (var c : o.content()) {
                if ("output_text".equals(c.type()) && c.text() != null) {
                    return c.text();
                }
            }
        }
        return null; // (선택) refusal 처리 추가 가능
    }

    public record Output(String id, String type, String role, List<Content> content) {

    }

    public record Content(String type, String text) {

    }
}
