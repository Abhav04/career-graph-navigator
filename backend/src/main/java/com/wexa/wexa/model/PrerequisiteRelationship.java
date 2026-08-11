package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Represents the prerequisite relationship between two Skills, including a strength coefficient.
 */
@RelationshipProperties
@Getter
@Setter
public class PrerequisiteRelationship {

  @RelationshipId private String id;

  @TargetNode private Skill skill;

  private double strength;
}
