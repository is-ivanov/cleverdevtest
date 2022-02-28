package com.iivanov.cleverdevtestnewsystem.dao;

import com.iivanov.cleverdevtestnewsystem.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
