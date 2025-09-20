package com.mc.backend.ai;


import com.mc.backend.ai.dto.ChatRequest;
import java.util.Map;
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
    public Map<String, Object> send(@RequestBody ChatRequest req) {
        return router.chatOnce(req.message());
    }
}
