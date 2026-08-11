package com.wexa.wexa.repository;

import java.util.List;

/** Projection interface for weighted learning path query results. */
public interface LearningPathProjection {
  String getTargetSkillName();

  List<String> getLearningPath();

  Integer getTotalHours();
}
