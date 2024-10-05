package com.project.notes_v2.service;

import com.project.notes_v2.exception.NotFoundException;
import com.project.notes_v2.exception.SaveException;
import com.project.notes_v2.model.Note;
import com.project.notes_v2.model.Tag;
import com.project.notes_v2.repository.NoteRepository;
import com.project.notes_v2.repository.TagRepository;

import com.project.notes_v2.repository.TagSpecification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;


    public List<Tag> getTagsByFilter(String name) {
        return tagRepository.findAll(TagSpecification.filterBy(name));
    }


    public List<Tag> getTagsByNoteId(Integer noteId) {
        Note note = this.noteRepository.findById(noteId)
                                       .orElseThrow( () -> new NotFoundException("Not found Note by id '" + noteId + "'"));
        return note.getTags();
    }


    public Tag createTag(String tagName) {
        Tag tagToCreate = new Tag();
        tagToCreate.setName(tagName);
        return this.saveTag(tagToCreate);
    }


    @Transactional
    public void deleteOrphanTags() {
        List<Tag> orphanTags = tagRepository.findAll().stream()
                                            .filter(tag -> tag.getNotes().isEmpty())
                                            .toList();
        tagRepository.deleteAll(orphanTags);
    }

    public void deleteOrphanTags(int noteId) {
        Note note = this.noteRepository.findById(noteId)
                                       .orElseThrow( () -> new NotFoundException("Not found Note by id '" + noteId + "'"));
        List<Tag> tags = note.getTags();
        // Get orphan tags
        tags.forEach(tag -> {
            if(tag.getNotes().isEmpty()) {
                tags.remove(tag);
                this.tagRepository.delete(tag);
            }
        });
    }


    /*---------------PRIVATE METHODS---------------*/

    private Tag saveTag(Tag tag) {
        try {
            return this.tagRepository.save(tag);
        } catch(SaveException exception) {
            throw new SaveException();
        }
    }

}
