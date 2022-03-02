package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.NoteRepository;
import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.NoteService;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.PatientService;
import com.iivanov.cleverdevtestnewsystem.webclients.ClientWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@EnableScheduling
public class NoteServiceImpl extends AbstractService<Note> implements NoteService {

    private final NoteRepository noteRepo;
    private final PatientService patientService;
    private final ClientWebClient clientWebClient;

    @Override
    @Scheduled(cron = "${cron.start-import-notes}")
    public void importNotesFromOldSystem() {
        List<Patient> activePatients = patientService.findActivePatients();
        Set<String> guidsActivePatients =
            patientService.getGuidsFromPatients(activePatients);
        List<ClientResponseDto> clientsFromOldSystem = clientWebClient.getClients();
        LocalDate dateTo = LocalDate.now();
        LocalDate dateFrom = dateTo.minusDays(1);
        List<ClientNoteRequestDto> requestDtosForNotes =
            clientsFromOldSystem.stream()
                .filter(client -> guidsActivePatients.contains(client.getGuid()))
                .map(client -> new ClientNoteRequestDto(client.getAgency(),
                    dateFrom, dateTo, client.getGuid()))
                .collect(Collectors.toList());


    }

    @Override
    protected JpaRepository<Note, Long> getRepo() {
        return noteRepo;
    }

    @Override
    protected String getEntityName() {
        return Note.class.getSimpleName();
    }

}
