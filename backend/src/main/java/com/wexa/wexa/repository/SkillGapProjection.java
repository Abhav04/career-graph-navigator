package com.wexa.wexa.repository;

/** Projection interface for skill gap query results. */
public interface SkillGapProjection {
  String getSkillName();

  Integer getImportance();

  Integer getHopDistance();
}
