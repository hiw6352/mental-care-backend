package com.mc.backend.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mc.backend.ai.enums.ChatMode;
import com.mc.backend.ai.enums.RiskLevel;

public record ChatMessageResponse(
    @JsonProperty("session_id") String sessionId,
    String reply,
    @JsonProperty("next_question") String nextQuestion,
    @JsonProperty("risk_level") RiskLevel riskLevel,
    Double confidence,
    ChatMode mode
) {

}