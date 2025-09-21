package com.mc.backend.chat.store;

import java.time.Instant;
import java.util.List;

public interface ConversationStore {

    List<ChatTurn> history(String conversationId);

    void append(String conversationId, ChatTurn turn);

    void reset(String conversationId);

    enum Role {USER, ASSISTANT}

    record ChatTurn(Role role, String content, Instant createdAt) {

    }
}
