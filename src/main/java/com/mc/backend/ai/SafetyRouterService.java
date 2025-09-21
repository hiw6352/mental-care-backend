package com.mc.backend.ai;

import com.mc.backend.ai.dto.ModerationResponse;
import com.mc.backend.ai.dto.RiskOnlyVerdict;
import com.mc.backend.ai.dto.RiskReply;
import com.mc.backend.ai.enums.ChatMode;
import com.mc.backend.ai.enums.RiskLevel;
import com.mc.backend.api.dto.ChatMessageRequest;
import com.mc.backend.api.dto.ChatMessageResponse;
import com.mc.backend.chat.store.ConversationStore;
import com.mc.backend.chat.store.ConversationStore.ChatTurn;
import com.mc.backend.chat.store.ConversationStore.Role;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SafetyRouterService {

    private final OpenAiGateway openai;
    private final double CONF_T;
    private final ConversationStore store;

    public SafetyRouterService(OpenAiGateway openai, ConversationStore store,
        @Value("${openai.safety.confidence-threshold}") double confT) {
        this.openai = openai;
        this.CONF_T = confT;
        this.store = store;
    }

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

    public ChatMessageResponse chatOnce(ChatMessageRequest req) {
        String sid = (req.sessionId() == null || req.sessionId().isBlank())
            ? UUID.randomUUID().toString()
            : req.sessionId();

        if (Boolean.TRUE.equals(req.reset())) {
            store.reset(sid);
        }

        // 0) Pre-moderation
        if (moderationHighRisk(openai.moderate(req.message()))) {
            return crisisTemplate(sid);
        }

        // 1) history + mini
        List<ChatTurn> history = store.history(sid);
        RiskReply rr = openai.classifyWithMini(history, req.message());

        // 2) escalate on risk
        boolean escalate =
            (rr.riskLevel() == RiskLevel.CONCERN || rr.riskLevel() == RiskLevel.CRISIS)
                && (rr.confidence() != null && rr.confidence() >= CONF_T);

        if (escalate) {
            RiskOnlyVerdict v = openai.verdictWithGpt5(req.message());
            if (v.riskLevel() == RiskLevel.CONCERN || v.riskLevel() == RiskLevel.CRISIS) {
                return crisisTemplate(sid);
            }
        }

        // 3) Post-moderation(출력)
        if (moderationHighRisk(openai.moderate(rr.reply()))) {
            return crisisTemplate(sid);
        }

        // 4) 저장
        store.append(sid, new ChatTurn(Role.USER, req.message(), Instant.now()));
        store.append(sid, new ChatTurn(Role.ASSISTANT, rr.reply(), Instant.now()));

        // 5) DTO로 반환
        return new ChatMessageResponse(
            sid, rr.reply(), rr.nextQuestion(), rr.riskLevel(), rr.confidence(), rr.mode()
        );
    }

    private ChatMessageResponse crisisTemplate(String sid) {
        String msg = "지금 많이 힘드신 것 같아요. 혼자가 아니에요. " +
            "긴급한 위험이 느껴지면 112/119로 연락하시고, " +
            "상담이 필요하면 1393(자살 예방 상담)으로 도움을 받아보실 수 있어요.";
        return new ChatMessageResponse(
            sid, msg, null, RiskLevel.CRISIS, 0.9, ChatMode.CRISIS
        );
    }
}
