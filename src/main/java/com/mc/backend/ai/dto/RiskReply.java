package com.mc.backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mc.backend.ai.enums.RiskLevel;

// 우리가 원하는 구조화 결과(JSON → 매핑)
public record RiskReply(
    String reply,
    @JsonProperty("next_question") String nextQuestion,
    @JsonProperty("risk_level") RiskLevel riskLevel,
    Double confidence
) {

}
