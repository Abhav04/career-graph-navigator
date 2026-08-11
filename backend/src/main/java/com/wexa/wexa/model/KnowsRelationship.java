package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Represents the relationship between a Person and a Skill they possess, including a proficiency
 * level.
 */
@RelationshipProperties
@Getter
@Setter
public class KnowsRelationship {

  @RelationshipId private String id;

  @TargetNode private Skill skill;

  private int proficiency;
}
