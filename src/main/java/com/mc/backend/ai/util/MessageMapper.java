package com.mc.backend.ai.util;

import com.mc.backend.ai.dto.responses.input.Message;
import com.mc.backend.chat.store.ConversationStore.ChatTurn;
import com.mc.backend.chat.store.ConversationStore.Role;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class MessageMapper {

    /**
     * system = input_text, user = input_text, assistant(history) = output_text
     */
    public static List<Message> toMessages(List<ChatTurn> history, String userText,
        String systemPrompt) {
        var msgs = new ArrayList<Message>();
        msgs.add(Message.system(systemPrompt)); // system -> input_text
        if (history != null) {
            for (var t : history) {
                if (t == null || t.content() == null) {
                    continue;
                }
                if (t.role() == Role.USER) {
                    msgs.add(Message.user(t.content()));      // input_text
                } else if (t.role() == Role.ASSISTANT) {
                    msgs.add(Message.assistant(t.content())); // output_text
                }
            }
        }
        msgs.add(Message.user(userText)); // 최신 유저 발화
        return msgs;
    }
}
