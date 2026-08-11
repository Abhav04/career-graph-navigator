package com.wexa.wexa.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Represents a Project node in the career navigation graph. A project demonstrates skills that a
 * user applied or learned.
 */
@Node
@Getter
@Setter
public class Project {

  @Id @GeneratedValue private String id;

  private String title;
  private String description;

  @Relationship(type = "DEMONSTRATES", direction = Relationship.Direction.OUTGOING)
  private List<Skill> demonstratedSkills;
}
