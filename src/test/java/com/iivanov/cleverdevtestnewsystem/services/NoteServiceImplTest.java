package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.NoteRepository;
import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import com.iivanov.cleverdevtestnewsystem.dto.NoteResponseDto;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.PatientService;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.UserService;
import com.iivanov.cleverdevtestnewsystem.webclients.ClientWebClient;
import com.iivanov.cleverdevtestnewsystem.webclients.NoteWebClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.iivanov.cleverdevtestnewsystem.util.TestObjects.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Captor
    ArgumentCaptor<Note> noteCaptor;

    @Mock
    private NoteRepository noteRepoMock;
    @Mock
    private PatientService patientServiceMock;
    @Mock
    private ClientWebClient clientWebClientMock;
    @Mock
    private NoteWebClient noteWebClientMock;
    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Nested
    @DisplayName("test 'importNotesFromOldSystem' method")
    class ImportNotesFromOldSystemTest {
        @Test
        @DisplayName("when in new system patient without notes then call noteRepo.save " +
            "with notes without id")
        void whenNewSystemWithoutNotes_CallRepoSaveWithNotesWithoutId() {
            Map<String, Patient> mapGuidPatientInNewSystem = createTestMapGuidPatient();
            List<ClientResponseDto> allClientsFromOldSystem = createTestListClients();
            List<ClientNoteRequestDto> bodiesForNotesRequest = createTestClientNoteRequestDtos();
            List<NoteResponseDto> notesFromOldSystem = createTestNoteResponseDtos();

            when(patientServiceMock.getPatientsWithGuids(any()))
                .thenReturn(mapGuidPatientInNewSystem);
            when(clientWebClientMock.getClients()).thenReturn(allClientsFromOldSystem);
            when(noteWebClientMock.getNotesByClients(bodiesForNotesRequest))
                .thenReturn(notesFromOldSystem);

            noteService.importNotesFromOldSystem();

            verify(noteRepoMock, times(notesFromOldSystem.size()))
                .save(noteCaptor.capture());

            List<Note> allValues = noteCaptor.getAllValues();

            assertThat(allValues).extracting(Note::getId)
                .containsOnlyNulls();

            Note savingNote1FromOldSystem = allValues.get(0);
            assertThat(savingNote1FromOldSystem.getOldNoteGuid())
                .isEqualTo(GUID_FIRST_NOTE);
            assertThat(savingNote1FromOldSystem.getContent())
                .isEqualTo(TEXT_FIRST_NOTE);

            Note savingNote2FromOldSystem = allValues.get(1);
            assertThat(savingNote2FromOldSystem.getOldNoteGuid())
                .isEqualTo(GUID_SECOND_NOTE);
            assertThat(savingNote2FromOldSystem.getContent())
                .isEqualTo(TEXT_SECOND_NOTE);
        }

        @Test
        @DisplayName("when in new system patients have notes which receive from " +
            "old system then should't add these notes")
        void whenNewSystemPatientsHaveNotesWhichReceiveFromOldSystem_NotAddTheseNotes() {
            Map<String, Patient> mapGuidPatientInNewSystem = createTestMapGuidPatient();
            List<ClientResponseDto> allClientsFromOldSystem = createTestListClients();
            List<ClientNoteRequestDto> bodiesForNotesRequest = createTestClientNoteRequestDtos();
            List<NoteResponseDto> notesFromOldSystem = createTestNoteResponseDtos();
            Note existingNote1 = createTestNote1();
            Patient existingPatient1 = mapGuidPatientInNewSystem.get(GUID_FIRST_PATIENT);
            existingPatient1.getNotes().add(existingNote1);


            when(patientServiceMock.getPatientsWithGuids(any()))
                .thenReturn(mapGuidPatientInNewSystem);
            when(clientWebClientMock.getClients()).thenReturn(allClientsFromOldSystem);
            when(noteWebClientMock.getNotesByClients(bodiesForNotesRequest))
                .thenReturn(notesFromOldSystem);


            noteService.importNotesFromOldSystem();

            verify(noteRepoMock, times(notesFromOldSystem.size() - 1))
                .save(noteCaptor.capture());

            List<Note> allValues = noteCaptor.getAllValues();

            assertThat(allValues).extracting(Note::getOldNoteGuid)
                .doesNotContain(GUID_FIRST_NOTE);
            assertThat(allValues).extracting(Note::getContent)
                .doesNotContain(TEXT_FIRST_NOTE);
        }
    }


}