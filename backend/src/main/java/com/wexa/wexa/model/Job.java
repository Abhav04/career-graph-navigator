package com.wexa.wexa.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Represents a Job node in the career navigation graph. A job requires certain skills and is
 * offered at a company.
 */
@Node
@Getter
@Setter
public class Job {

  @Id @GeneratedValue private String id;

  private String title;
  private String seniorityLevel;

  @Relationship(type = "REQUIRES", direction = Relationship.Direction.OUTGOING)
  private List<RequiresRelationship> requiredSkills;

  @Relationship(type = "AT", direction = Relationship.Direction.OUTGOING)
  private Company company;
}
