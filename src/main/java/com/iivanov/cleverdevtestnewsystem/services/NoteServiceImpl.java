package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.NoteRepository;
import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import com.iivanov.cleverdevtestnewsystem.dto.NoteResponseDto;
import com.iivanov.cleverdevtestnewsystem.entities.User;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.NoteService;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.PatientService;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.UserService;
import com.iivanov.cleverdevtestnewsystem.webclients.ClientWebClient;
import com.iivanov.cleverdevtestnewsystem.webclients.NoteWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NoteServiceImpl extends AbstractService<Note> implements NoteService {

    private static final AtomicInteger counterCreateNotes = new AtomicInteger(0);
    private static final AtomicInteger counterUpdateNotes = new AtomicInteger(0);
    /*
     * Т.к. старая система фильтрует и отдаёт заметки по дате создания,
     * DateCompareUtils.isDateInPeriod(note.getCreatedDateTime().toLocalDate(), from, to)
     * то нам необходимо постоянно загружать все заметки,
     * поэтому в запрос для старой системы в поле 'dateTo' надо подставлять
     * дату создания первой заметки. Её надо посмотреть и внести в эту константу.
     * Если бы старая система фильтровала заметки по времени последнего изменения,
     * то 'dateTo' можно было бы ставить вчерашний день. Т.к. обновляем каждые 2 часа,
     * но передаём в теле запроса не дату и время, а только дату
     * */
    public static final LocalDate DATE_CREATE_FIRST_NOTE_IN_OLD_SYSTEM =
        LocalDate.of(2010, 1, 1);

    private final NoteRepository noteRepo;
    private final PatientService patientService;
    private final ClientWebClient clientWebClient;
    private final NoteWebClient noteWebClient;
    private final UserService userService;

    @Override
    @Scheduled(cron = "${cron.start-import-notes}")
    public void importNotesFromOldSystem() {
        Map<String, Patient> guidsActivePatients = getActivePatients();
        List<NoteResponseDto> notesFromServer = getNotesFromOldSystem(guidsActivePatients);
        if (notesFromServer.size() == 0) {
            return;
        }
        Map<String, Note> existingNotesActivePatients = new HashMap<>();
        guidsActivePatients.forEach((guid, patient) -> {
            Set<Note> notes = patient.getNotes();
            notes.forEach(note ->
                existingNotesActivePatients.put(note.getOldNoteGuid(), note));
        });
        saveNotesInNewSystem(guidsActivePatients, notesFromServer,
            existingNotesActivePatients);
        log.info("Create {} notes, update {} notes", counterCreateNotes,
            counterUpdateNotes);
        counterCreateNotes.set(0);
        counterUpdateNotes.set(0);
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
        List<NoteResponseDto> notesFromOldSystem = noteWebClient
            .getNotesByClients(bodiesForRequestForNotes);
        log.info("{} notes received from old system", notesFromOldSystem.size());
        return notesFromOldSystem;
    }

    private List<ClientNoteRequestDto> prepareRequestsBodies(
        Map<String, Patient> guidsActivePatients) {
        List<ClientResponseDto> clientsFromOldSystem = clientWebClient.getClients();
        LocalDate dateTo = LocalDate.now();
        return clientsFromOldSystem.stream()
            .filter(client -> guidsActivePatients.containsKey(client.getGuid()))
            .map(client -> new ClientNoteRequestDto(client.getAgency(),
                DATE_CREATE_FIRST_NOTE_IN_OLD_SYSTEM, dateTo, client.getGuid()))
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
            !isPatientEquals(noteDto, existingNote);
    }

    private void update(NoteResponseDto noteDto, Patient patient, Note existingNote) {
        if (noteDto.getModifiedDateTime().isAfter(existingNote.getModifiedDateTime())) {

            existingNote.setContent(noteDto.getComments());
            existingNote.setModifiedDateTime(noteDto.getModifiedDateTime());
            existingNote.setCreatedDateTime(noteDto.getCreatedDateTime());
            User user =
                userService.findByLoginOrCreateIfMissing(noteDto.getLoggedUser());
            existingNote.setUserCreator(user);
            existingNote.setUserEditor(user);
            existingNote.setPatient(patient);
            noteRepo.save(existingNote);
            counterUpdateNotes.getAndIncrement();
        }
    }

    private void create(NoteResponseDto noteDto, Patient patient) {
        User user =
            userService.findByLoginOrCreateIfMissing(noteDto.getLoggedUser());
        Note newNote = new Note();
        newNote.setCreatedDateTime(noteDto.getCreatedDateTime());
        newNote.setModifiedDateTime(noteDto.getModifiedDateTime());
        newNote.setUserCreator(user);
        newNote.setUserEditor(user);
        newNote.setContent(noteDto.getComments());
        newNote.setPatient(patient);
        newNote.setOldNoteGuid(noteDto.getGuid());
        noteRepo.save(newNote);
        counterCreateNotes.getAndIncrement();
    }

    private boolean isPatientEquals(NoteResponseDto noteDto, Note existingNote) {
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
