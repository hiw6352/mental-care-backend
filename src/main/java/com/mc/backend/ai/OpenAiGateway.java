package com.mc.backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.backend.ai.dto.ModerationRequest;
import com.mc.backend.ai.dto.ModerationResponse;
import com.mc.backend.ai.dto.ResponseFormat;
import com.mc.backend.ai.dto.ResponsesEnvelope;
import com.mc.backend.ai.dto.ResponsesRequest;
import com.mc.backend.ai.dto.RiskOnlySchema;
import com.mc.backend.ai.dto.RiskOnlyVerdict;
import com.mc.backend.ai.dto.RiskReply;
import com.mc.backend.ai.dto.RiskReplySchema;
import com.mc.backend.config.ExternalWebClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class OpenAiGateway {

    private final WebClient client;
    private final ObjectMapper om;
    private final String primaryModel;
    private final String escalationModel;

    public OpenAiGateway(
        ExternalWebClientFactory factory,
        ObjectMapper om,
        @Value("${openai.primary-model}") String primaryModel,
        @Value("${openai.escalation-model}") String escalationModel
    ) {
        this.client = factory.get("openai");
        this.om = om;
        this.primaryModel = primaryModel;
        this.escalationModel = escalationModel;
    }

    // Moderation
    public ModerationResponse moderate(String text) {
        var req = new ModerationRequest("omni-moderation-latest", text);
        try {
            return client.post().uri("/moderations")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .block();
        } catch (Exception e) {
            // 폴백: 실패 시 flagged=false 취급
            return new ModerationResponse(
                java.util.List.of(new ModerationResponse.Result(false, null)));
        }
    }

    // mini: 구조화 응답
    public RiskReply classifyWithMini(String systemPrompt, String userText) {
        String input = systemPrompt + "\n\n사용자: " + userText;

        var schema = new RiskReplySchema();
        var req = ResponsesRequest.of(
            primaryModel,
            input,
            ResponseFormat.jsonSchema("RiskReply", schema, true),
            400
        );

        ResponsesEnvelope env = client.post().uri("/responses")
            .bodyValue(req)
            .retrieve()
            .bodyToMono(ResponsesEnvelope.class)
            .block();

        String json = env != null ? env.firstText() : null;
        if (json == null || json.isBlank()) {
            return new RiskReply("지금은 답변을 준비할 수 없어요. 잠시 후 다시 시도해 주세요.",
                null, com.mc.backend.ai.enums.RiskLevel.WATCH, 0.5);
        }
        try {
            return om.readValue(json, RiskReply.class);
        } catch (Exception ex) {
            return new RiskReply("잠시 후 다시 시도해 주세요.",
                null, com.mc.backend.ai.enums.RiskLevel.WATCH, 0.5);
        }
    }

    // GPT-5: 리스크만 2차 판정
    public RiskOnlyVerdict verdictWithGpt5(String userText) {
        String sys = """
            당신은 안전 판정 어시스턴트입니다.
            입력 텍스트의 위험도를 'ok|watch|concern|crisis' 중 하나로 평가하고
            confidence(0~1)를 제공합니다. 출력은 JSON 스키마만 허용합니다.
            """;

        var schema = new RiskOnlySchema();
        var req = ResponsesRequest.of(
            escalationModel,
            sys + "\n\n텍스트: " + userText,
            ResponseFormat.jsonSchema("RiskOnly", schema, true),
            120
        );

        ResponsesEnvelope env = client.post().uri("/responses")
            .bodyValue(req)
            .retrieve()
            .bodyToMono(ResponsesEnvelope.class)
            .block();

        String json = env != null ? env.firstText() : null;
        if (json == null) {
            return new RiskOnlyVerdict(com.mc.backend.ai.enums.RiskLevel.WATCH, 0.5);
        }
        try {
            return om.readValue(json, RiskOnlyVerdict.class);
        } catch (Exception e) {
            return new RiskOnlyVerdict(com.mc.backend.ai.enums.RiskLevel.WATCH, 0.5);
        }
    }
}
