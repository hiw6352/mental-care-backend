package com.mc.backend.ai.dto;

import java.util.List;

public record ModerationResponse(List<Result> results) {

    public record Result(boolean flagged, ModerationCategories categories) {

    }
}
