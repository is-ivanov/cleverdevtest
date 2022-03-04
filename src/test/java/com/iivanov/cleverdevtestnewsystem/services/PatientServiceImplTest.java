package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.PatientRepository;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.iivanov.cleverdevtestnewsystem.util.TestObjects.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository repoMock;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Nested
    @DisplayName("test 'getPatientsWithGuids' method")
    class GetPatientsWithGuidsTest {
        @Test
        @DisplayName("when non-empty list with patients with one guid then " +
            "should return expected map with the same size")
        void whenNonEmptyListPatientsOneGuid_ReturnExpectedMapWithTheSameSize() {
            List<Patient> patients = createTestListPatients();
            Patient patient1 = patients.get(0);
            Patient patient2 = patients.get(1);

            Map<String, Patient> actualMap =
                patientService.getPatientsWithGuids(patients);

            assertThat(actualMap).hasSize(patients.size());
            assertThat(actualMap.get(GUID_FIRST_PATIENT)).isEqualTo(patient1);
            assertThat(actualMap.get(GUID_SECOND_PATIENT)).isEqualTo(patient2);
        }

        @Test
        @DisplayName("when non-empty list with patients with several guids then " +
            "should return expected map with size more then size List")
        void whenNonEmptyListPatientsSeveralGuids_ReturnExpectedMapWithSizeMoreThenSizeList() {
            List<Patient> patients = createTestListPatients();
            Set<Note> notes = new HashSet<>();
            Patient patient3 = new Patient(NAME_THIRD_PATIENT,
                LAST_NAME_THIRD_PATIENT, GUID_THIRD_PATIENT, (short) 34,
                notes);
            Patient patient1 = patients.get(0);
            Patient patient2 = patients.get(1);
            patients.add(patient3);

            Map<String, Patient> actualMap =
                patientService.getPatientsWithGuids(patients);

            assertThat(actualMap).hasSize(patients.size() + 1);
            assertThat(actualMap.get(GUID_FIRST_PATIENT)).isEqualTo(patient1);
            assertThat(actualMap.get(GUID_SECOND_PATIENT)).isEqualTo(patient2);
            assertThat(actualMap.get(GUID_THIRD_PATIENT)).isNull();
            assertThat(actualMap.get(FIRST_GUID_THIRD_PATIENT)).isEqualTo(patient3);
            assertThat(actualMap.get(SECOND_GUID_THIRD_PATIENT)).isEqualTo(patient3);
        }
    }

    @Nested
    @DisplayName("test 'findActivePatients' method")
    class FindActivePatientsTest {
        @Test
        @DisplayName("when repo return list of patients then should return this list")
        void whenRepoReturnListOfPatients_ReturnThisList() {
            List<Patient> testListPatients = createTestListPatients();

            when(repoMock.findActivePatients()).thenReturn(testListPatients);

            assertThat(patientService.findActivePatients()).isEqualTo(testListPatients);
        }
    }



}