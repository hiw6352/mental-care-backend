package com.mc.backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 필요한 카테고리만 정의, 나머지는 무시
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationCategories {

    @JsonProperty("self-harm")
    private boolean selfHarm;
    @JsonProperty("self-harm/intent")
    private boolean selfHarmIntent;
    @JsonProperty("self-harm/instructions")
    private boolean selfHarmInstructions;

    public boolean isSelfHarm() {
        return selfHarm || selfHarmIntent || selfHarmInstructions;
    }
}
