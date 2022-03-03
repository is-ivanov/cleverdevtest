package com.iivanov.cleverdevtestnewsystem.dao;

import com.iivanov.cleverdevtestnewsystem.entities.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {

    Optional<CompanyUser> findByLogin(String login);

}
