package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Represents a LearningResource node in the career navigation graph (e.g. course, book, tutorial).
 */
@Node
@Getter
@Setter
public class LearningResource {

  @Id @GeneratedValue private String id;

  private String title;
  private String url;
  private String resourceType;
}
