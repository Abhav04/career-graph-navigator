package com.wexa.wexa.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Represents a Person node in the career navigation graph. A person has skills (via
 * KnowsRelationship) and has built projects.
 */
@Node
@Getter
@Setter
public class Person {

  @Id @GeneratedValue private String id;

  private String name;
  private String currentRole;

  @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING)
  private List<KnowsRelationship> skills;

  @Relationship(type = "BUILT", direction = Relationship.Direction.OUTGOING)
  private List<Project> builtProjects;
}
