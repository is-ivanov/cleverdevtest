package com.iivanov.cleverdevtestnewsystem.services.interfaces;

import com.iivanov.cleverdevtestnewsystem.entities.Patient;

import java.util.List;
import java.util.Set;

public interface PatientService extends Service<Patient> {

    List<Patient> findActivePatients();

    Set<String> getGuidsFromPatients(List<Patient> patients);
}
