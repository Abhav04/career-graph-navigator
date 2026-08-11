package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Represents a Company node in the career navigation graph. A company belongs to an industry
 * sector.
 */
@Node
@Getter
@Setter
public class Company {

  @Id @GeneratedValue private String id;

  private String name;

  @Relationship(type = "IN_INDUSTRY", direction = Relationship.Direction.OUTGOING)
  private Industry industry;
}
