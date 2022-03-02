package com.iivanov.cleverdevtestnewsystem.dao;

import com.iivanov.cleverdevtestnewsystem.config.BaseRepoTest;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/sql/patient-repo-test-data.sql")
class PatientRepositoryTest extends BaseRepoTest {

    public static final String FIRSTNAME_FIRST_PATIENT = "First";
    public static final String FIRSTNAME_SECOND_PATIENT = "Second";
    public static final String FIRSTNAME_THIRD_PATIENT = "Third";

    @Autowired
    private PatientRepository repo;

    @Nested
    @DisplayName("test 'findActivePatients' method")
    class FindActivePatientsTest {
        @Test
        @DisplayName("default should return 3 patients")
        void defaultShouldReturn3Patients() {
            List<Patient> activePatients = repo.findActivePatients();

            assertThat(activePatients).hasSize(3);
            assertThat(activePatients).extracting(Patient::getId)
                .containsOnly(1L, 2L, 3L)
                .doesNotContain(4L);
            assertThat(activePatients).extracting(Patient::getFirstName)
                .containsOnly(FIRSTNAME_FIRST_PATIENT, FIRSTNAME_SECOND_PATIENT,
                    FIRSTNAME_THIRD_PATIENT);
        }
    }
}