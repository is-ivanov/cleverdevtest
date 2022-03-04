package com.iivanov.cleverdevtestnewsystem.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${url-old-system}")
    private String urlOldSystem;

    @Value("${timeout-millis-requests}")
    private int timeout;

    @Bean
    public WebClient webClientWithTimeout() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();
        final HttpClient httpClient = HttpClient.create()
            .wiretap("reactor.netty.http.client.HttpClient",
                LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
            .responseTimeout(Duration.ofMillis(timeout))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(timeout,
                        TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(timeout,
                        TimeUnit.MILLISECONDS)));

        return WebClient.builder()
            .baseUrl(urlOldSystem)
            .defaultHeaders(headers -> {
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            })
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .build();
    }
}
