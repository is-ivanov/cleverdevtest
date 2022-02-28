package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.NoteRepository;
import com.iivanov.cleverdevtestnewsystem.entities.Note;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NoteServiceImpl extends AbstractService<Note> implements NoteService {

    private final NoteRepository noteRepo;

    @Override
    public Note update(int id, Note entity) {
        return null;
    }

    @Override
    protected JpaRepository<Note, Long> getRepo() {
        return noteRepo;
    }

    @Override
    protected String getEntityName() {
        return Note.class.getSimpleName();
    }

}
