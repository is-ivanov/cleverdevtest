package com.iivanov.cleverdevtestnewsystem.util;

import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import com.iivanov.cleverdevtestnewsystem.dto.NoteResponseDto;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class TestObjects {
    public static final String NAME_FIRST_PATIENT = "Ivan";
    public static final String LAST_NAME_FIRST_PATIENT = "Ivanov";
    public static final String GUID_FIRST_PATIENT =
        "17DD0E94-6A57-49C8-E933-CC480D1AC3FB";
    public static final String AGENCY_FIRST_PATIENT = "Dry";
    public static final String NAME_SECOND_PATIENT = "Petr";
    public static final String LAST_NAME_SECOND_PATIENT = "Petrov";
    public static final String GUID_SECOND_PATIENT =
        "A661E400-8CD0-6336-D0C2-2E8012903819";
    public static final String AGENCY_SECOND_PATIENT = "Fond";
    public static final String NAME_THIRD_PATIENT = "John";
    public static final String LAST_NAME_THIRD_PATIENT = "Smith";
    public static final String GUID_THIRD_PATIENT =
        "4FED669C-7FAF-0EA3-28EC-44F7173B18E6,176FF906-BA91-1DAC-BF82-C466BEEC23A9";
    public static final String FIRST_GUID_THIRD_PATIENT =
        "4FED669C-7FAF-0EA3-28EC-44F7173B18E6";
    public static final String SECOND_GUID_THIRD_PATIENT =
        "176FF906-BA91-1DAC-BF82-C466BEEC23A9";
    public static final LocalDate DATE_CREATE_FIRST_NOTE_IN_OLD_SYSTEM =
        LocalDate.of(2010, 1, 1);
    public static final String TEXT_FIRST_NOTE = "text first note";
    public static final String TEXT_SECOND_NOTE = "text second note";
    public static final String GUID_FIRST_NOTE =
        "02323732-4567-3882-0128-C68D0150A170";
    public static final String GUID_SECOND_NOTE =
        "AC6ADAD7-78A4-3F64-8645-6AD5B51F4F18";
    public static final LocalDateTime MODIFIED_DATE_TIME_FIRST_NOTE =
        LocalDateTime.of(2022, 2, 15, 5, 15);
    public static final LocalDateTime MODIFIED_DATE_TIME_SECOND_NOTE =
        LocalDateTime.of(2022, 1, 10, 15, 20);
    public static final String LOGIN_FIRST_USER = "firstLogin";
    private static final Long ID_FIRST_NOTE = 15L;
    private static final LocalDateTime CREATED_DATE_TIME_FIRST_NOTE =
        LocalDateTime.of(2021, 12, 22, 9, 40);


    public static Patient createTestPatient1() {
        return new Patient(NAME_FIRST_PATIENT,
            LAST_NAME_FIRST_PATIENT, GUID_FIRST_PATIENT, (short) 1,
            createTestEmptySetNotes());
    }

    public static Patient createTestPatient2() {
        return new Patient(NAME_SECOND_PATIENT,
            LAST_NAME_SECOND_PATIENT, GUID_SECOND_PATIENT, (short) 2,
            createTestEmptySetNotes());
    }

    public static List<Patient> createTestListPatients() {
        Patient patient1 = createTestPatient1();
        Patient patient2 = createTestPatient2();
        return new ArrayList<>(Arrays.asList(patient1, patient2));
    }

    public static Map<String, Patient> createTestMapGuidPatient() {
        Patient patient1 = createTestPatient1();
        Patient patient2 = createTestPatient2();
        Map<String, Patient> result = new HashMap<>();
        result.put(GUID_FIRST_PATIENT, patient1);
        result.put(GUID_SECOND_PATIENT, patient2);
        return result;
    }

    public static Set<Note> createTestEmptySetNotes() {
        return new HashSet<>();
    }

    public static ClientResponseDto createTestClient1() {
        return ClientResponseDto.builder()
            .agency(AGENCY_FIRST_PATIENT)
            .guid(GUID_FIRST_PATIENT)
            .build();
    }

    public static ClientResponseDto createTestClient2() {
        return ClientResponseDto.builder()
            .agency(AGENCY_SECOND_PATIENT)
            .guid(GUID_SECOND_PATIENT)
            .build();
    }

    public static List<ClientResponseDto> createTestListClients() {
        ClientResponseDto client1 = createTestClient1();
        ClientResponseDto client2 = createTestClient2();
        return new ArrayList<>(Arrays.asList(client1, client2));
    }

    public static ClientNoteRequestDto createTestClientNoteRequestDto1() {
        return ClientNoteRequestDto.builder()
            .agency(AGENCY_FIRST_PATIENT)
            .dateFrom(DATE_CREATE_FIRST_NOTE_IN_OLD_SYSTEM)
            .dateTo(LocalDate.now())
            .clientGuid(GUID_FIRST_PATIENT)
            .build();
    }

    public static ClientNoteRequestDto createTestClientNoteRequestDto2() {
        return ClientNoteRequestDto.builder()
            .agency(AGENCY_SECOND_PATIENT)
            .dateFrom(DATE_CREATE_FIRST_NOTE_IN_OLD_SYSTEM)
            .dateTo(LocalDate.now())
            .clientGuid(GUID_SECOND_PATIENT)
            .build();
    }

    public static List<ClientNoteRequestDto> createTestClientNoteRequestDtos() {
        ClientNoteRequestDto clientNoteRequestDto1 = createTestClientNoteRequestDto1();
        ClientNoteRequestDto clientNoteRequestDto2 = createTestClientNoteRequestDto2();
        return new ArrayList<>(Arrays.asList(clientNoteRequestDto1, clientNoteRequestDto2));
    }

    public static NoteResponseDto createTestNoteResponseDto1() {
        return NoteResponseDto.builder()
            .comments(TEXT_FIRST_NOTE)
            .guid(GUID_FIRST_NOTE)
            .createdDateTime(CREATED_DATE_TIME_FIRST_NOTE)
            .modifiedDateTime(MODIFIED_DATE_TIME_FIRST_NOTE)
            .clientGuid(GUID_FIRST_PATIENT)
            .loggedUser(LOGIN_FIRST_USER)
            .build();
    }

    public static NoteResponseDto createTestNoteResponseDto2() {
        return NoteResponseDto.builder()
            .comments(TEXT_SECOND_NOTE)
            .guid(GUID_SECOND_NOTE)
            .modifiedDateTime(MODIFIED_DATE_TIME_SECOND_NOTE)
            .clientGuid(GUID_FIRST_PATIENT)
            .loggedUser(LOGIN_FIRST_USER)
            .build();
    }

    public static List<NoteResponseDto> createTestNoteResponseDtos() {
        NoteResponseDto noteResponseDto1 = createTestNoteResponseDto1();
        NoteResponseDto noteResponseDto2 = createTestNoteResponseDto2();
        return new ArrayList<>(Arrays.asList(noteResponseDto1, noteResponseDto2));
    }

    public static Note createTestNote1() {
        Note result = new Note();
        result.setId(ID_FIRST_NOTE);
        result.setCreatedDateTime(CREATED_DATE_TIME_FIRST_NOTE);
        result.setModifiedDateTime(MODIFIED_DATE_TIME_FIRST_NOTE);
        User user1 = new User(LOGIN_FIRST_USER);
        result.setUserCreator(user1);
        result.setUserEditor(user1);
        result.setContent(TEXT_FIRST_NOTE);
        result.setPatient(createTestPatient1());
        result.setOldNoteGuid(GUID_FIRST_NOTE);
        return result;
    }

}
