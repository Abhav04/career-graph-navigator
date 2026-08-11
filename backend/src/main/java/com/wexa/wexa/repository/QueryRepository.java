package com.wexa.wexa.repository;

import com.wexa.wexa.model.Person;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for executing complex graph path-finding and gap analysis queries in the
 * career-navigation application.
 */
public interface QueryRepository extends Neo4jRepository<Person, String> {

  /**
   * Identifies skills required by a target job that the specified person does not already know, and
   * calculates the shortest path distance (1-4 hops) from any skill they know to each missing
   * skill.
   *
   * @param personName the name of the person
   * @param jobTitle the title of the target job
   * @return a list of maps containing skillName, importance, and hopDistance
   */
  @Query(
      "MATCH (p:Person {name: $personName}), (j:Job {title: $jobTitle})\n"
          + "MATCH (j)-[r:REQUIRES]->(requiredSkill:Skill)\n"
          + "WHERE size([ (p)-[:KNOWS]->(requiredSkill) | 1 ]) = 0\n"
          + "OPTIONAL MATCH path = shortestPath((knownSkill)-[:PREREQUISITE_OF*1..4]->(requiredSkill))\n"
          + "WHERE knownSkill IS NULL OR size([ (p)-[:KNOWS]->(knownSkill) | 1 ]) > 0\n"
          + "WITH requiredSkill, r.importance AS importance, path\n"
          + "RETURN requiredSkill.name AS skillName, importance, coalesce(min(length(path)), 9999) AS hopDistance\n"
          + "ORDER BY hopDistance ASC, importance DESC")
  List<SkillGapProjection> findSkillGap(
      @Param("personName") String personName, @Param("jobTitle") String jobTitle);

  /**
   * Finds the path of prerequisite skills (up to 6 hops) with the lowest total learning hours (via
   * LEARNED_VIA properties) leading from any skill the person already knows to each required skill
   * they do not know.
   *
   * @param personName the name of the person
   * @param jobTitle the title of the target job
   * @return a list of maps containing targetSkillName, learningPath, and totalHours
   */
  @Query(
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
  List<LearningPathProjection> findWeightedLearningPath(
      @Param("personName") String personName, @Param("jobTitle") String jobTitle);
}
