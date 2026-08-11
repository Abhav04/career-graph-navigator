package com.wexa.wexa.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Represents a Skill node in the career navigation graph. A skill can have prerequisites (other
 * skills) and can be learned via learning resources.
 */
@Node
@Getter
@Setter
public class Skill {

  @Id @GeneratedValue private String id;

  private String name;
  private String category;

  @Relationship(type = "PREREQUISITE_OF", direction = Relationship.Direction.OUTGOING)
  private List<PrerequisiteRelationship> prerequisites;

  @Relationship(type = "LEARNED_VIA", direction = Relationship.Direction.OUTGOING)
  private List<LearnedViaRelationship> resources;
}
