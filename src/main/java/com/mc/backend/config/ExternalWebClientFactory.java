package com.mc.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
public class ExternalWebClientFactory {

    private final WebClient.Builder baseBuilder;
    private final ExternalApisProperties props;
    private final ConcurrentMap<String, WebClient> cache = new ConcurrentHashMap<>();

    public ExternalWebClientFactory(WebClient.Builder baseBuilder, ExternalApisProperties props) {
        this.baseBuilder = baseBuilder;
        this.props = props;
    }

    public WebClient get(String name) {
        return cache.computeIfAbsent(name, this::buildClient);
    }

    private WebClient buildClient(String name) {
        ExternalApisProperties.Api cfg = Objects.requireNonNull(props.getClients().get(name),
            "Unknown external api client: " + name);

        // 클라이언트별 타임아웃(있으면 덮어쓰기)
        WebClient.Builder b = baseBuilder.clone()
            .baseUrl(cfg.getBaseUrl());

        // per-client timeout 적용
        Integer t = cfg.getTimeoutMs();
        if (t != null && t > 0) {
            HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, t)
                .responseTimeout(Duration.ofMillis(t))
                .doOnConnected(conn -> conn.addHandlerLast(
                    new ReadTimeoutHandler(t, java.util.concurrent.TimeUnit.MILLISECONDS)));
            b.clientConnector(new ReactorClientHttpConnector(httpClient));
        }

        // 헤더
        if (cfg.getHeaders() != null) {
            cfg.getHeaders().forEach(b::defaultHeader);
        }
        // API 키 → Authorization Bearer 자동 주입(있을 때만)
        if (cfg.getApiKey() != null && !cfg.getApiKey().isBlank()) {
            b.defaultHeader("Authorization", "Bearer " + cfg.getApiKey());
        }

        return b.build();
    }
}
