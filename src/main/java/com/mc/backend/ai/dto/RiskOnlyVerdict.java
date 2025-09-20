package com.mc.backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mc.backend.ai.enums.RiskLevel;

public record RiskOnlyVerdict(
    @JsonProperty("risk_level") RiskLevel riskLevel,
    Double confidence
) {

}
