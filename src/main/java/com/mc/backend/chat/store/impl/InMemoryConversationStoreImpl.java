package com.mc.backend.chat.store.impl;

import com.mc.backend.chat.store.ConversationStore;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryConversationStoreImpl implements ConversationStore {

    private static final int MAX_TURNS = 20;
    private final Map<String, Deque<ChatTurn>> store = new ConcurrentHashMap<>();

    @Override
    public List<ChatTurn> history(String conversationId) {
        return new ArrayList<>(store.computeIfAbsent(conversationId, k -> new ArrayDeque<>()));
    }

    @Override
    public void append(String conversationId, ChatTurn turn) {
        var q = store.computeIfAbsent(conversationId, k -> new ArrayDeque<>());
        if (q.size() >= MAX_TURNS) {
            q.pollFirst();
        }
        q.addLast(turn.createdAt() == null
            ? new ChatTurn(turn.role(), turn.content(), Instant.now())
            : turn);
    }

    @Override
    public void reset(String conversationId) {
        store.remove(conversationId);
    }
}
