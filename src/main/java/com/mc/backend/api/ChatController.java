package com.mc.backend.api;


import com.mc.backend.ai.SafetyRouterService;
import com.mc.backend.api.dto.ChatMessageRequest;
import com.mc.backend.api.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SafetyRouterService router;

    @PostMapping("/messages")
    public ChatMessageResponse send(@RequestBody ChatMessageRequest req) {
        return router.chatOnce(req);
    }
}
