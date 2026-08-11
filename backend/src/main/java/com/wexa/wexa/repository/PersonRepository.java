package com.wexa.wexa.repository;

import com.wexa.wexa.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/** Neo4j Repository for Person entity node. */
public interface PersonRepository extends Neo4jRepository<Person, String> {
  @org.springframework.data.neo4j.repository.query.Query("MATCH (p:Person) RETURN p")
  java.util.List<Person> findAll();
}
