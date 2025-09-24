package com.mc.backend.ai.util;

import com.mc.backend.ai.dto.ResponsesEnvelope;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ResponseParser {

    /**
     * 모델 응답에서 output_text를 1순위로 반환. 없으면 null
     */
    public static String firstText(ResponsesEnvelope env) {
        if (env == null || env.output() == null) {
            return null;
        }
        for (var o : env.output()) {
            if (o == null || o.content() == null) {
                continue;
            }
            for (var c : o.content()) {
                if (c == null) {
                    continue;
                }
                if ("output_text".equals(c.type()) && c.text() != null && !c.text().isBlank()) {
                    return c.text();
                }
            }
        }
        return null;
    }
}
