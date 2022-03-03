package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.PatientRepository;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl extends AbstractService<Patient> implements
    PatientService {

    public static final String GUID_DELIMITER = ",";

    private final PatientRepository patientRepo;

    @Override
    public List<Patient> findActivePatients() {
        log.debug("Find active patients");
        return patientRepo.findActivePatients();
    }

    @Override
    public Map<String, Patient> getPatientsWithGuids(List<Patient> patients) {
        Map<String, Patient> guidsOfPatients = new HashMap<>();
        patients.stream()
            .filter(patient -> patient.getOldClientGuid() != null)
            .forEach(patient -> {
                String oldClientGuidsString = patient.getOldClientGuid();
                String[] arrayGuids = oldClientGuidsString.split(GUID_DELIMITER);
                Arrays.stream(arrayGuids).forEach(guid ->
                    guidsOfPatients.put(guid, patient));
            });
        return guidsOfPatients;
    }

    @Override
    protected JpaRepository<Patient, Long> getRepo() {
        return patientRepo;
    }

    @Override
    protected String getEntityName() {
        return Patient.class.getSimpleName();
    }


}
