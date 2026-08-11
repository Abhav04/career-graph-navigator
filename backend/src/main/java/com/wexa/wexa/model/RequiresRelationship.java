package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Represents the requirement relationship between a Job and a Skill, indicating the importance
 * level of the skill for the job.
 */
@RelationshipProperties
@Getter
@Setter
public class RequiresRelationship {

  @RelationshipId private String id;

  @TargetNode private Skill skill;

  private int importance;
}
