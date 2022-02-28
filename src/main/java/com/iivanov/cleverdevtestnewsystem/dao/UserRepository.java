package com.iivanov.cleverdevtestnewsystem.dao;

import com.iivanov.cleverdevtestnewsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
