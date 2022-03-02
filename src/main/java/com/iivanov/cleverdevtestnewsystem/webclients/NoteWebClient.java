package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NoteWebClient {

    public static final String URI_NOTES = "/notes";

    private final WebClient webClient;

    public void getNotesByClient(final ClientNoteRequestDto dto) {


        Mono<Object[]> response = webClient.post()
            .uri(URI_NOTES)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .retrieve()
            .bodyToMono(Object[].class)
            .log();



    }
}
