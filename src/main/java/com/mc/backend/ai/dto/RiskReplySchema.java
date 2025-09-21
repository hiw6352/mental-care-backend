package com.mc.backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RiskReplySchema implements JsonSchema.SchemaRoot {

    private final String type = "object";
    private final Properties properties = new Properties();
    // 🔧 strict=true이므로 모든 키가 required에 포함되어야 함
    private final String[] required = new String[]{"reply", "next_question", "risk_level",
        "confidence", "mode"};
    @JsonProperty("additionalProperties")
    private final boolean additionalProperties = false;

    public String getType() {
        return type;
    }

    public Properties getProperties() {
        return properties;
    }

    public String[] getRequired() {
        return required;
    }

    public boolean isAdditionalProperties() {
        return additionalProperties;
    }

    public static class Properties {

        public final StringType reply = new StringType();
        @JsonProperty("next_question")
        public final StringType next_question = new StringType();
        @JsonProperty("risk_level")
        public final EnumString risk_level =
            new EnumString(new String[]{"ok", "watch", "concern", "crisis"});
        public final NumberRange confidence = new NumberRange(0.0, 1.0);
        public final EnumString mode =
            new EnumString(new String[]{"casual", "supportive", "counseling", "crisis"});
    }

    public static class StringType {

        public final String type = "string";
    }

    public static class NumberRange {

        public final String type = "number";
        public final Double minimum;
        public final Double maximum;

        public NumberRange(Double min, Double max) {
            this.minimum = min;
            this.maximum = max;
        }
    }

    public static class EnumString {

        public final String type = "string";
        @JsonProperty("enum")
        public final String[] values;

        public EnumString(String[] values) {
            this.values = values;
        }
    }
}
