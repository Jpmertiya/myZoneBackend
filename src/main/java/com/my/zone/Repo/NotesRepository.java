package com.my.zone.Repo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.my.zone.Models.Notes;

@Repository
public interface NotesRepository extends MongoRepository<Notes, ObjectId> {

	List<Notes> findByUserId(String Nid);
//
//	@Query("{ 'userId' : ?0, 'status' : 'active' }")
//	List<Notes> findActiveDocumentsByUserId(String userId);
}
