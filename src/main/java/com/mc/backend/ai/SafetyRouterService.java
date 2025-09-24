package com.mc.backend.ai;

import com.mc.backend.ai.dto.ModerationResponse;
import com.mc.backend.ai.dto.RiskOnlyVerdict;
import com.mc.backend.ai.dto.RiskReply;
import com.mc.backend.ai.enums.ChatMode;
import com.mc.backend.ai.enums.RiskLevel;
import com.mc.backend.ai.util.RiskGate;
import com.mc.backend.api.dto.ChatMessageRequest;
import com.mc.backend.api.dto.ChatMessageResponse;
import com.mc.backend.chat.store.ConversationStore;
import com.mc.backend.chat.store.ConversationStore.ChatTurn;
import com.mc.backend.chat.store.ConversationStore.Role;
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
            ? java.util.UUID.randomUUID().toString()
            : req.sessionId();

        if (Boolean.TRUE.equals(req.reset())) {
            store.reset(sid);
        }

        if (moderationHighRisk(openai.moderate(req.message()))) {
            return crisisTemplate(sid);
        }

        var history = store.history(sid);
        RiskReply rr = openai.classifyWithMini(history, req.message());

        boolean escalate = RiskGate.shouldEscalate(rr.riskLevel(), rr.confidence(), CONF_T);
        if (escalate) {
            RiskOnlyVerdict v = openai.verdictWithGpt5(req.message());
            if (v.riskLevel() == RiskLevel.CONCERN || v.riskLevel() == RiskLevel.CRISIS) {
                return crisisTemplate(sid);
            }
        }

        if (moderationHighRisk(openai.moderate(rr.reply()))) {
            return crisisTemplate(sid);
        }

        store.append(sid, new ChatTurn(Role.USER, req.message(), java.time.Instant.now()));
        store.append(sid, new ChatTurn(Role.ASSISTANT, rr.reply(), java.time.Instant.now()));

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
