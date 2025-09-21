package com.mc.backend.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
    @JsonProperty("session_id") String sessionId, // 없으면 서버가 생성
    @NotBlank String message,
    Boolean reset
) {

}
