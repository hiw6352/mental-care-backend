package com.mc.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
@EnableConfigurationProperties(ExternalApisProperties.class)
public class HttpClientConfig {

    @Bean
    public WebClient.Builder baseWebClientBuilder(
        @Value("${external.default-timeout-ms:15000}") int defaultTimeoutMs
    ) {
        HttpClient httpClient = HttpClient.create()
//            .wiretap("reactor.netty.http.client.HttpClient",
//                LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, defaultTimeoutMs)
            .responseTimeout(Duration.ofMillis(defaultTimeoutMs))
            .doOnConnected(conn -> conn.addHandlerLast(
                new ReadTimeoutHandler(defaultTimeoutMs, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(logRequest())
            .filter(logResponse());
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            System.out.println("HTTP " + req.method() + " " + req.url());
            log.info("========== WebClient HTTP LOG REQUEST INFO     ==========");
            log.info("URL : {}", req.url());
            log.info("METHOD : {}", req.method());
            log.info("Content-Type : {}", req.headers().get("Content-Type"));
            log.info("Authorization : {}", req.headers().get("Authorization"));
//            log.info("BODY : {}", req.body().toString()) todo: 나중에 body 로그는 어떻게 할지 고민 필요
            log.info("========== WebClient HTTP LOG REQUEST INFO END ==========");
            return Mono.just(req);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> res
            .bodyToMono(String.class)
            .flatMap(body -> {
                // 응답 로그
                log.info("========== WebClient HTTP LOG RESPONSE INFO     ==========");
                log.info("URL : {}", res.request().getURI());
                log.info("METHOD : {}", res.request().getMethod());
                log.info("STATUS : {}", res.statusCode());
                log.info("BODY : {}", body);
                log.info("========== WebClient HTTP LOG RESPONSE INFO END ==========");

                // body를 다시 복원해서 내려주지 않으면 downstream에서 못 읽음
                return Mono.just(
                    ClientResponse.from(res)
                        .body(body)  // String으로 다시 넣음
                        .build()
                );
            }));
    }
}
