package ru.ufanet.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.domain.Note;

import java.util.List;

public interface NoteRepo extends JpaRepository<Note, Long> {
    List<Note> findAllByUserIdOrderByIdAsc(long id);
}
