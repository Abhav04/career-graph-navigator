package com.wexa.wexa.service;

import com.wexa.wexa.repository.JobRepository;
import com.wexa.wexa.repository.PersonRepository;
import com.wexa.wexa.repository.QueryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service class for career path analytics. Delegates the calls directly to the repository layer.
 */
@Service
public class CareerPathService {

  private final QueryRepository queryRepository;
  private final PersonRepository personRepository;
  private final JobRepository jobRepository;
  private final org.springframework.data.neo4j.core.Neo4jClient neo4jClient;

  /**
   * Constructs the service with the repositories.
   *
   * @param queryRepository the repository to delegate database queries to
   * @param personRepository the repository for Person entities
   * @param jobRepository the repository for Job entities
   * @param neo4jClient the Neo4jClient bean for raw query execution
   */
  public CareerPathService(
      QueryRepository queryRepository,
      PersonRepository personRepository,
      JobRepository jobRepository,
      org.springframework.data.neo4j.core.Neo4jClient neo4jClient) {
    this.queryRepository = queryRepository;
    this.personRepository = personRepository;
    this.jobRepository = jobRepository;
    this.neo4jClient = neo4jClient;
  }

  /**
   * Retrieves the skill gap details between a person and a job.
   *
   * @param personName the name of the person
   * @param jobTitle the title of the target job
   * @return the list of skill gap details
   */
  public List<Map<String, Object>> getSkillGap(String personName, String jobTitle) {
    return new java.util.ArrayList<>(
        neo4jClient
            .query(
                "MATCH (p:Person {name: $personName}), (j:Job {title: $jobTitle})\n"
                    + "MATCH (j)-[r:REQUIRES]->(requiredSkill:Skill)\n"
                    + "WHERE size([ (p)-[:KNOWS]->(requiredSkill) | 1 ]) = 0\n"
                    + "OPTIONAL MATCH path = shortestPath((knownSkill)-[:PREREQUISITE_OF*1..4]->(requiredSkill))\n"
                    + "WHERE knownSkill IS NULL OR size([ (p)-[:KNOWS]->(knownSkill) | 1 ]) > 0\n"
                    + "WITH requiredSkill, r.importance AS importance, path\n"
                    + "RETURN requiredSkill.name AS skillName, importance, coalesce(min(length(path)), 9999) AS hopDistance\n"
                    + "ORDER BY hopDistance ASC, importance DESC")
            .bind(personName)
            .to("personName")
            .bind(jobTitle)
            .to("jobTitle")
            .fetch()
            .all());
  }

  /**
   * Retrieves the optimal learning path of prerequisite skills between a person and a job.
   *
   * @param personName the name of the person
   * @param jobTitle the title of the target job
   * @return the list of learning paths
   */
  public List<Map<String, Object>> getShortestLearningPath(String personName, String jobTitle) {
    return new java.util.ArrayList<>(
        neo4jClient
            .query(
                "MATCH (p:Person {name: $personName}), (j:Job {title: $jobTitle})\n"
                    + "MATCH (j)-[:REQUIRES]->(requiredSkill:Skill)\n"
                    + "WHERE size([ (p)-[:KNOWS]->(requiredSkill) | 1 ]) = 0\n"
                    + "MATCH path = (knownSkill:Skill)-[:PREREQUISITE_OF*1..6]->(requiredSkill:Skill)\n"
                    + "WHERE size([ (p)-[:KNOWS]->(knownSkill) | 1 ]) > 0\n"
                    + "UNWIND nodes(path)[1..] AS skillToLearn\n"
                    + "OPTIONAL MATCH (skillToLearn)-[lv:LEARNED_VIA]->(:LearningResource)\n"
                    + "WITH path, requiredSkill, skillToLearn, min(coalesce(lv.estHours, 0)) AS skillHours\n"
                    + "WITH path, requiredSkill, sum(skillHours) AS totalHours\n"
                    + "ORDER BY requiredSkill, totalHours ASC\n"
                    + "WITH requiredSkill, collect({path: path, totalHours: totalHours})[0] AS bestPath\n"
                    + "RETURN requiredSkill.name AS targetSkillName,\n"
                    + "       [n IN nodes(bestPath.path) | n.name] AS learningPath,\n"
                    + "       bestPath.totalHours AS totalHours")
            .bind(personName)
            .to("personName")
            .bind(jobTitle)
            .to("jobTitle")
            .fetch()
            .all());
  }

  public List<String> getAllPeopleNames() {
    return new java.util.ArrayList<>(
        neo4jClient.query("MATCH (p:Person) RETURN p.name AS name").fetch().all().stream()
            .map(m -> (String) m.get("name"))
            .collect(Collectors.toList()));
  }

  /**
   * Retrieves a list of all Job titles.
   *
   * @return a list of job title strings
   */
  public List<String> getAllJobTitles() {
    return new java.util.ArrayList<>(
        neo4jClient.query("MATCH (j:Job) RETURN j.title AS title").fetch().all().stream()
            .map(m -> (String) m.get("title"))
            .collect(Collectors.toList()));
  }
}
