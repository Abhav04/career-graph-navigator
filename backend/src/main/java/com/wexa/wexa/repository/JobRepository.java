package com.wexa.wexa.repository;

import com.wexa.wexa.model.Job;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/** Neo4j Repository for Job entity node. */
public interface JobRepository extends Neo4jRepository<Job, String> {
  @org.springframework.data.neo4j.repository.query.Query("MATCH (j:Job) RETURN j")
  java.util.List<Job> findAll();
}
