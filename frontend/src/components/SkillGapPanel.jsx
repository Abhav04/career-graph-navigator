import LoadingState from "./LoadingState";
import ErrorState from "./ErrorState";
import EmptyState from "./EmptyState";

/**
 * SkillGapPanel renders the results of the required skill gaps.
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
export default function SkillGapPanel({
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
        <p>Select a person and a job above to analyze skill gaps.</p>
      </div>
    );
  }

  if (loading) {
    return <LoadingState message="Calculating required skill gaps..." />;
  }

  if (error) {
    return <ErrorState error={error} onRetry={onRetry} />;
  }

  if (!data || data.length === 0) {
    return (
      <EmptyState
        message={`${targetPerson} already possesses all the skills required for ${targetJob}!`}
      />
    );
  }

  return (
    <div className="panel-content">
      <div className="skills-list">
        {data.map((item, idx) => {
          const skillName = item.skillToLearn || item.skillName;
          const hopDistance =
            item.hopsFromKnownSkill !== undefined
              ? item.hopsFromKnownSkill
              : item.hopDistance;
          const isReachable = hopDistance !== 9999;
          const importance = item.importance;

          // Short human-readable label for hop distance
          let hopLabel = "No direct path";
          if (isReachable) {
            if (hopDistance === 1) {
              hopLabel = "Direct next step";
            } else if (hopDistance >= 2 && hopDistance <= 3) {
              hopLabel = "A few steps away";
            } else {
              hopLabel = "Longer-term";
            }
          }

          // Class for importance indicator dot
          let importanceClass = "low";
          if (importance >= 8) {
            importanceClass = "high";
          } else if (importance >= 5) {
            importanceClass = "medium";
          }

          return (
            <div key={idx} className="skill-card polished">
              <div className="skill-card-main">
                <div className="skill-info">
                  <span className="skill-name-prominent">{skillName}</span>
                  <div className="skill-meta">
                    <span className={`importance-dot ${importanceClass}`} />
                    <span className={`importance-text ${importanceClass}`}>
                      Importance: {importance}/10
                    </span>
                  </div>
                </div>
                <div className="hop-badge-container">
                  <span
                    className={`hop-label-text ${isReachable ? "reachable" : "unreachable"}`}
                  >
                    {hopLabel}
                  </span>
                  {isReachable && (
                    <span className="hop-count-sub">
                      ({hopDistance} hop{hopDistance > 1 ? "s" : ""})
                    </span>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
