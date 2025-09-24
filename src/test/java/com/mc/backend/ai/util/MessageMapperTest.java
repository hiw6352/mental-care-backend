package com.mc.backend.ai.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mc.backend.chat.store.ConversationStore.ChatTurn;
import com.mc.backend.chat.store.ConversationStore.Role;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class MessageMapperTest {

    @Test
    void mapsRolesToTypes() {
        var hist = List.of(
            new ChatTurn(Role.USER, "안녕", Instant.now()),
            new ChatTurn(Role.ASSISTANT, "반가워요", Instant.now())
        );
        var msgs = MessageMapper.toMessages(hist, "요즘 어때?", "SYS");
        assertEquals("input_text", msgs.get(0).content().get(0).type()); // system
        assertEquals("input_text", msgs.get(1).content().get(0).type()); // user
        assertEquals("output_text", msgs.get(2).content().get(0).type()); // assistant
        assertEquals("input_text", msgs.get(3).content().get(0).type()); // latest user
    }
}
