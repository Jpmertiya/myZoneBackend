package com.my.zone.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.my.zone.Models.Notes;

public interface NotesRepository extends MongoRepository<Notes, String> {

}
