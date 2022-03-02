package com.iivanov.cleverdevtestnewsystem.services.interfaces;

import com.iivanov.cleverdevtestnewsystem.entities.Note;

public interface NoteService extends Service<Note> {

    void importNotesFromOldSystem();
}
