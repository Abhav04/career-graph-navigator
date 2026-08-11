import { useState } from "react";

/**
 * PersonJobSelector provides dropdowns for selecting a person and a job,
 * and a submit button to trigger path analysis.
 *
 * @param {{
 *   people: string[],
 *   jobs: string[],
 *   loadingPeople: boolean,
 *   loadingJobs: boolean,
 *   onAnalyze: function
 * }} props
 */
export default function PersonJobSelector({
  people = [],
  jobs = [],
  loadingPeople = false,
  loadingJobs = false,
  onAnalyze,
}) {
  const [selectedPerson, setSelectedPerson] = useState("");
  const [selectedJob, setSelectedJob] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (selectedPerson && selectedJob) {
      onAnalyze(selectedPerson, selectedJob);
    }
  };

  const isSubmitDisabled = !selectedPerson || !selectedJob;

  return (
    <form className="selector-form" onSubmit={handleSubmit}>
      <div className="form-group-grid">
        <div className="form-group">
          <label htmlFor="person-select" className="form-label">
            Select Person
          </label>
          <select
            id="person-select"
            className="form-select"
            value={selectedPerson}
            onChange={(e) => setSelectedPerson(e.target.value)}
            disabled={loadingPeople}
          >
            <option value="">
              {loadingPeople ? "Loading people..." : "-- Choose Person --"}
            </option>
            {people.map((name) => (
              <option key={name} value={name}>
                {name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="job-select" className="form-label">
            Target Job Role
          </label>
          <select
            id="job-select"
            className="form-select"
            value={selectedJob}
            onChange={(e) => setSelectedJob(e.target.value)}
            disabled={loadingJobs}
          >
            <option value="">
              {loadingJobs ? "Loading jobs..." : "-- Choose Job --"}
            </option>
            {jobs.map((title) => (
              <option key={title} value={title}>
                {title}
              </option>
            ))}
          </select>
        </div>
      </div>

      <button
        type="submit"
        className="btn btn-primary submit-btn"
        disabled={isSubmitDisabled}
      >
        Analyze Career Path
      </button>
    </form>
  );
}
