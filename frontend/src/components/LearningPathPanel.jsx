import { Fragment } from "react";
import LoadingState from "./LoadingState";
import ErrorState from "./ErrorState";
import EmptyState from "./EmptyState";

/**
 * LearningPathPanel renders the optimal learning paths for required skills.
 *
 * @param {{
 *   loading: boolean,
 *   error: Error|string,
 *   data: any[],
 *   targetPerson: string,
 *   targetJob: string,
 *   onRetry: function
 * }} props
 */
export default function LearningPathPanel({
  loading = false,
  error = null,
  data = null,
  targetPerson = "",
  targetJob = "",
  onRetry,
}) {
  if (!targetPerson || !targetJob) {
    return (
      <div className="panel-initial">
        <p>Select a person and a job above to map optimal learning paths.</p>
      </div>
    );
  }

  if (loading) {
    return <LoadingState message="Mapping optimal learning paths..." />;
  }

  if (error) {
    return <ErrorState error={error} onRetry={onRetry} />;
  }

  if (!data || data.length === 0) {
    return (
      <EmptyState
        message={`No learning paths needed. ${targetPerson} already has all required skills!`}
      />
    );
  }

  return (
    <div className="panel-content">
      <div className="paths-list">
        {data.map((item, idx) => {
          const targetSkill = item.targetSkill || item.targetSkillName;
          const pathNodes = item.learningPath || [];
          const estimatedHours =
            item.estimatedHours !== undefined
              ? item.estimatedHours
              : item.totalHours;

          return (
            <div key={idx} className="path-card polished">
              <div className="path-card-target-label">
                Path to <strong>{targetSkill}</strong>
              </div>

              {pathNodes.length > 0 ? (
                <div className="path-sequence-wrapper">
                  <div className="path-sequence horizontal-flow">
                    {pathNodes.map((nodeName, nodeIdx) => {
                      const isStart = nodeIdx === 0;
                      const isEnd = nodeIdx === pathNodes.length - 1;

                      return (
                        <Fragment key={nodeIdx}>
                          {nodeIdx > 0 && (
                            <span className="path-arrow-icon">→</span>
                          )}
                          <div
                            className={`path-node-pill ${isStart ? "start" : ""} ${
                              isEnd ? "end" : ""
                            }`}
                          >
                            <span className="node-pill-text">{nodeName}</span>
                            {isStart && (
                              <span className="node-pill-label">Known</span>
                            )}
                            {isEnd && (
                              <span className="node-pill-label">Target</span>
                            )}
                          </div>
                        </Fragment>
                      );
                    })}
                  </div>

                  <div className="path-total-hours-card">
                    <span className="total-hours-value">{estimatedHours}</span>
                    <span className="total-hours-label">Estimated Hours</span>
                  </div>
                </div>
              ) : (
                <p className="no-path-text">
                  No learning sequence available for this skill.
                </p>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
