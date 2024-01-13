package com.my.zone.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.my.zone.Models.User;

public interface UserRepository extends MongoRepository<User, String> {

}
