package com.iivanov.cleverdevtestnewsystem.dao;

import com.iivanov.cleverdevtestnewsystem.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p WHERE p.status IN (200, 210, 230)")
    List<Patient> findActivePatients();

}
