package com.wexa.wexa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/** Represents an Industry node in the career navigation graph. */
@Node
@Getter
@Setter
public class Industry {

  @Id @GeneratedValue private String id;

  private String name;
}
