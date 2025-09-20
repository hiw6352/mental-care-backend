package com.mc.backend.ai.dto;

import java.util.List;

public record ResponsesEnvelope(List<Output> output) {

    public String firstText() {
        if (output == null || output.isEmpty()) {
            return null;
        }
        var cont = output.get(0).content();
        if (cont == null || cont.isEmpty()) {
            return null;
        }
        return cont.get(0).text();
    }

    public record Output(String id, String type, String role, List<Content> content) {

    }

    public record Content(String type, String text) {

    }
}
