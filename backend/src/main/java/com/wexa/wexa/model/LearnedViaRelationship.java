package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Represents the learning path relationship between a Skill and a LearningResource, including
 * estimated hours and difficulty rating.
 */
@RelationshipProperties
@Getter
@Setter
public class LearnedViaRelationship {

  @RelationshipId private String id;

  @TargetNode private LearningResource learningResource;

  private int estHours;
  private int difficulty;
}
