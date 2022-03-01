package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NoteClient {

    public static final String URI_NOTES = "/notes";
    private final WebClient webClient;

    public void getNotesByClient(final ClientNoteRequestDto dto) {


//        webClient.post()
//            .uri(URI_NOTES)
//            .bodyValue()

    }
}
