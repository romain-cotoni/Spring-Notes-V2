package com.project.notes_v2.mapper;

import com.project.notes_v2.dto.NoteRequestDTO;
import com.project.notes_v2.dto.NoteResponseDTO;
import com.project.notes_v2.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note toNote(NoteRequestDTO noteRequestDTO);

    NoteResponseDTO toNoteResponseDTO(Note note);

    void updateNoteFromDTO(NoteRequestDTO noteRequestDTO, @MappingTarget Note note);

    List<NoteResponseDTO> toNoteResponseDTOList(List<Note> notes);
}
