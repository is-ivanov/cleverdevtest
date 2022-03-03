package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientWebClient {

    public static final String URI_CLIENTS = "/clients";

    private final WebClient webClient;

    public List<ClientResponseDto> getClients() {
        Mono<ClientResponseDto[]> response = webClient.post()
            .uri(URI_CLIENTS)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(ClientResponseDto[].class).log();
        ClientResponseDto[] clientResponses = response.block();
        assert clientResponses != null;
        return Arrays.stream(clientResponses).collect(Collectors.toList());
    }
}
