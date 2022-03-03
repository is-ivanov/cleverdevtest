package com.iivanov.cleverdevtestnewsystem.services.interfaces;

import com.iivanov.cleverdevtestnewsystem.entities.Patient;

import java.util.List;
import java.util.Map;

public interface PatientService extends Service<Patient> {

    List<Patient> findActivePatients();

    Map<String, Patient> getPatientsWithGuids(List<Patient> patients);
}
