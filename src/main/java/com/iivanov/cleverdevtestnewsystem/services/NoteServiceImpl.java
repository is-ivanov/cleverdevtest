package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.NoteRepository;
import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import com.iivanov.cleverdevtestnewsystem.dto.NoteResponseDto;
import com.iivanov.cleverdevtestnewsystem.entities.CompanyUser;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.NoteService;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.PatientService;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.UserService;
import com.iivanov.cleverdevtestnewsystem.webclients.ClientWebClient;
import com.iivanov.cleverdevtestnewsystem.webclients.NoteWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final NoteWebClient noteWebClient;
    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void doStart() {
        importNotesFromOldSystem();
    }

    @Override
//    @Scheduled(cron = "${cron.start-import-notes}")
    public void importNotesFromOldSystem() {
        Map<String, Patient> guidsActivePatients = getActivePatients();
        List<NoteResponseDto> notesFromServer = getNotesFromOldSystem(guidsActivePatients);
        //TODO add check size notes
        Map<String, Note> existingNotesActivePatients = new HashMap<>();
        guidsActivePatients.forEach((guid, patient) -> {
            Set<Note> notes = patient.getNotes();
            notes.forEach(note ->
                existingNotesActivePatients.put(note.getOldNoteGuid(), note));
        });
        saveNotesInNewSystem(guidsActivePatients, notesFromServer,
            existingNotesActivePatients);
    }

    @Override
    protected JpaRepository<Note, Long> getRepo() {
        return noteRepo;
    }

    @Override
    protected String getEntityName() {
        return Note.class.getSimpleName();
    }

    private void saveNotesInNewSystem(Map<String, Patient> guidsActivePatients,
                                      List<NoteResponseDto> notesFromServer,
                                      Map<String, Note> existingNotesActivePatients) {
        notesFromServer.forEach(noteDto -> {
            String guidNoteDto = noteDto.getGuid();
            Patient patient = guidsActivePatients.get(noteDto.getClientGuid());
            if (existingNotesActivePatients.containsKey(guidNoteDto)) {
                Note existingNote = existingNotesActivePatients.get(guidNoteDto);
                if (checkDifferences(noteDto, existingNote)) {
                    update(noteDto, patient, existingNote);
                }
            } else {
                create(noteDto, patient);
            }
        });
    }

    private List<NoteResponseDto> getNotesFromOldSystem(
        Map<String, Patient> guidsActivePatients) {
        List<ClientNoteRequestDto> bodiesForRequestForNotes =
            prepareRequestsBodies(guidsActivePatients);
        return noteWebClient.getNotesByClients(bodiesForRequestForNotes);
    }

    private List<ClientNoteRequestDto> prepareRequestsBodies(
        Map<String, Patient> guidsActivePatients) {
        List<ClientResponseDto> clientsFromOldSystem = clientWebClient.getClients();
        LocalDate dateTo = LocalDate.now();
        LocalDate dateFrom = dateTo.minusDays(1);
        return clientsFromOldSystem.stream()
            .filter(client -> guidsActivePatients.containsKey(client.getGuid()))
            .map(client -> new ClientNoteRequestDto(client.getAgency(),
                dateFrom, dateTo, client.getGuid()))
            .collect(Collectors.toList());
    }

    private Map<String, Patient> getActivePatients() {
        List<Patient> activePatients = patientService.findActivePatients();
        return patientService.getPatientsWithGuids(activePatients);
    }

    private boolean checkDifferences(NoteResponseDto noteDto, Note existingNote) {
        return !isContentEquals(noteDto, existingNote) ||
            !isModifiedDateTimeEquals(noteDto, existingNote) ||
            !isCreatedDateTimeEquals(noteDto, existingNote) ||
            !isUserEquals(noteDto, existingNote) ||
            !isClientEquals(noteDto, existingNote);
    }

    private void update(NoteResponseDto noteDto, Patient patient, Note existingNote) {
        if (noteDto.getModifiedDateTime().isAfter(existingNote.getModifiedDateTime())) {

            existingNote.setContent(noteDto.getComments());
            existingNote.setModifiedDateTime(noteDto.getModifiedDateTime());
            existingNote.setCreatedDateTime(noteDto.getCreatedDateTime());
            CompanyUser user =
                userService.findByLoginAndCreateIfMissing(noteDto.getLoggedUser());
            existingNote.setUserCreator(user);
            existingNote.setUserEditor(user);
            existingNote.setPatient(patient);
            noteRepo.save(existingNote);
        }
    }

    private void create(NoteResponseDto noteDto, Patient patient) {
        CompanyUser companyUser =
            userService.findByLoginAndCreateIfMissing(noteDto.getLoggedUser());
        Note newNote = new Note();
        newNote.setCreatedDateTime(noteDto.getCreatedDateTime());
        newNote.setModifiedDateTime(noteDto.getModifiedDateTime());
        newNote.setUserCreator(companyUser);
        newNote.setUserEditor(companyUser);
        newNote.setContent(noteDto.getComments());
        newNote.setPatient(patient);
        newNote.setOldNoteGuid(noteDto.getGuid());
        noteRepo.save(newNote);
    }

    private boolean isClientEquals(NoteResponseDto noteDto, Note existingNote) {
        return existingNote.getPatient().getOldClientGuid().contains(noteDto.getClientGuid());
    }

    private boolean isUserEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getLoggedUser().equals(existingNote.getUserEditor().getLogin());
    }

    private boolean isCreatedDateTimeEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getCreatedDateTime().equals(existingNote.getCreatedDateTime());
    }

    private boolean isModifiedDateTimeEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getModifiedDateTime().equals(existingNote.getModifiedDateTime());
    }

    private boolean isContentEquals(NoteResponseDto noteDto, Note existingNote) {
        return noteDto.getComments().equals(existingNote.getContent());
    }

}
