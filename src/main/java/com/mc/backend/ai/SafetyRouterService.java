package com.mc.backend.ai;

import com.mc.backend.ai.dto.ModerationResponse;
import com.mc.backend.ai.dto.RiskOnlyVerdict;
import com.mc.backend.ai.dto.RiskReply;
import com.mc.backend.ai.enums.RiskLevel;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SafetyRouterService {

    private final OpenAiGateway openai;
    private final double CONF_T;

    public SafetyRouterService(OpenAiGateway openai,
        @Value("${openai.safety.confidence-threshold:0.6}") double confT) {
        this.openai = openai;
        this.CONF_T = confT;
    }

    private static final String SYSTEM_PROMPT = """
        당신은 공감적이고 비판단적인 멘탈헬스 보조 챗봇입니다.
        - 의료적 진단/치료 약속은 하지 않습니다.
        - 위기 신호가 강하면 즉시 '도움 안내'를 권유합니다.
        - 한국어로 3~6문장, 따뜻하고 간결하게 답하세요.
        - 출력은 JSON 스키마(reply/next_question/risk_level/confidence)에 맞춰야 합니다.
        """;

    private boolean moderationHighRisk(ModerationResponse m) {
        if (m == null || m.results() == null || m.results().isEmpty()) {
            return false;
        }
        var r = m.results().get(0);
        if (r.flagged()) {
            return true;
        }
        var cats = r.categories();
        return cats != null && cats.isSelfHarm();
    }

    public Map<String, Object> chatOnce(String userText) {
        // 0) Pre-moderation
        if (moderationHighRisk(openai.moderate(userText))) {
            return crisisTemplate();
        }

        // 1) mini
        RiskReply rr = openai.classifyWithMini(SYSTEM_PROMPT, userText);

        boolean escalate =
            (rr.riskLevel() == RiskLevel.CONCERN || rr.riskLevel() == RiskLevel.CRISIS)
                && (rr.confidence() != null && rr.confidence() >= CONF_T);

        if (escalate) {
            RiskOnlyVerdict v = openai.verdictWithGpt5(userText);
            if (v.riskLevel() == RiskLevel.CONCERN || v.riskLevel() == RiskLevel.CRISIS) {
                return crisisTemplate();
            }
        }

        // 2) Post-moderation(출력)
        if (moderationHighRisk(openai.moderate(rr.reply()))) {
            return crisisTemplate();
        }

        return Map.of(
            "reply", rr.reply(),
            "next_question", rr.nextQuestion(),
            "risk_level", rr.riskLevel(), // Enum → JSON은 소문자로 직렬화
            "confidence", rr.confidence()
        );
    }

    private Map<String, Object> crisisTemplate() {
        String msg = "지금 많이 힘드신 것 같아요. 혼자가 아니에요. " +
            "긴급한 위험이 느껴지면 112/119에 바로 연락하시고, " +
            "상담이 필요하면 1393(자살 예방 상담)으로 도움을 받아보실 수 있어요.";
        return Map.of(
            "reply", msg,
            "risk_level", RiskLevel.CRISIS,
            "confidence", 0.9
        );
    }
}
