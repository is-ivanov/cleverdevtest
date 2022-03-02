package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.PatientRepository;
import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl extends AbstractService<Patient> implements PatientService {

    public static final String GUID_DELIMITER = ",";
    private final PatientRepository patientRepo;

    @Override
    public List<Patient> findActivePatients() {
        log.debug("Find active patients");
        return patientRepo.findActivePatients();
    }

    @Override
    public Set<String> getGuidsFromPatients(List<Patient> patients) {
        Set<String> guidsOfPatients = new HashSet<>();
        patients.stream()
            .filter(patient -> patient.getOldClientGuid() != null)
            .forEach(patient -> {
                String oldClientGuidsString = patient.getOldClientGuid();
                String[] arrayGuids = oldClientGuidsString.split(GUID_DELIMITER);
                guidsOfPatients.addAll(Arrays.asList(arrayGuids));
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
