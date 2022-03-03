package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import com.iivanov.cleverdevtestnewsystem.dto.NoteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoteWebClient {

    public static final String URI_NOTES = "/notes";

    private final WebClient webClient;

    public List<NoteResponseDto> getNotesByClients(final List<ClientNoteRequestDto> listDtos) {
        return Flux.fromIterable(listDtos)
            .flatMap(this::getNoteByClient)
            .collectList()
            .block();
    }

    private Flux<NoteResponseDto> getNoteByClient(final ClientNoteRequestDto dto) {
        return webClient.post()
            .uri(URI_NOTES)
            .bodyValue(dto)
            .retrieve()
            .bodyToFlux(NoteResponseDto.class);
    }
}
