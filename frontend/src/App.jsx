import { useState, useEffect } from "react";
import {
  fetchPeople,
  fetchJobs,
  fetchGapAnalysis,
  fetchShortestPath,
} from "./api/careerPathApi";
import PersonJobSelector from "./components/PersonJobSelector";
import SkillGapPanel from "./components/SkillGapPanel";
import LearningPathPanel from "./components/LearningPathPanel";
import "./App.css";

function App() {
  // Initial lists
  const [people, setPeople] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [loadingInitial, setLoadingInitial] = useState(true);
  const [initialError, setInitialError] = useState(null);

  // Database metadata
  const [dbName, setDbName] = useState("neo4j");
  const [dbStatus, setDbStatus] = useState("connected");

  // Active query parameters
  const [targetPerson, setTargetPerson] = useState("");
  const [targetJob, setTargetJob] = useState("");

  // Independent panel states
  const [gapData, setGapData] = useState(null);
  const [loadingGap, setLoadingGap] = useState(false);
  const [errorGap, setErrorGap] = useState(null);

  const [pathData, setPathData] = useState(null);
  const [loadingPath, setLoadingPath] = useState(false);
  const [errorPath, setErrorPath] = useState(null);

  // Fetch initial setup lists
  const loadInitialData = async () => {
    setLoadingInitial(true);
    setInitialError(null);
    try {
      // Fetch health to get active database name and status
      try {
        const healthRes = await fetch("http://localhost:8080/api/health");
        if (healthRes.ok) {
          const healthJson = await healthRes.json();
          setDbName(healthJson.database || "neo4j");
          setDbStatus(healthJson.status || "connected");
        }
      } catch (healthErr) {
        console.error("Health check failed", healthErr);
      }

      const [peopleList, jobsList] = await Promise.all([
        fetchPeople(),
        fetchJobs(),
      ]);
      setPeople(peopleList);
      setJobs(jobsList);
    } catch (err) {
      setInitialError(err);
    } finally {
      setLoadingInitial(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadInitialData();
  }, []);

  // Parallel fetch triggers
  const handleAnalyze = (person, job) => {
    setTargetPerson(person);
    setTargetJob(job);

    // 1. Fetch Skill Gap details
    setLoadingGap(true);
    setErrorGap(null);
    fetchGapAnalysis(person, job)
      .then((data) => setGapData(data))
      .catch((err) => setErrorGap(err))
      .finally(() => setLoadingGap(false));

    // 2. Fetch Learning Path details in parallel
    setLoadingPath(true);
    setErrorPath(null);
    fetchShortestPath(person, job)
      .then((data) => setPathData(data))
      .catch((err) => setErrorPath(err))
      .finally(() => setLoadingPath(false));
  };

  // Re-run handlers for retries
  const handleRetryGap = () => {
    if (targetPerson && targetJob) {
      setLoadingGap(true);
      setErrorGap(null);
      fetchGapAnalysis(targetPerson, targetJob)
        .then((data) => setGapData(data))
        .catch((err) => setErrorGap(err))
        .finally(() => setLoadingGap(false));
    }
  };

  const handleRetryPath = () => {
    if (targetPerson && targetJob) {
      setLoadingPath(true);
      setErrorPath(null);
      fetchShortestPath(targetPerson, targetJob)
        .then((data) => setPathData(data))
        .catch((err) => setErrorPath(err))
        .finally(() => setLoadingPath(false));
    }
  };

  return (
    <div className="dashboard-container">
      {/* Sidebar Panel */}
      <aside className="dashboard-sidebar">
        <div className="sidebar-brand">
          <svg
            className="brand-icon"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          >
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
          </svg>
          <div className="brand-text">
            <h2>WEXA</h2>
            <span>Career Graph Engine</span>
          </div>
        </div>

        <div className="sidebar-section">
          <h4 className="section-label">Configuration</h4>
          <PersonJobSelector
            people={people}
            jobs={jobs}
            loadingPeople={loadingInitial}
            loadingJobs={loadingInitial}
            onAnalyze={handleAnalyze}
          />
        </div>

        {/* Database Status Section */}
        <div className="sidebar-section system-status">
          <h4 className="section-label">Graph Diagnostics</h4>
          <div className="status-card">
            <div className="status-row">
              <span className="status-label">Database</span>
              <span className="status-value">{dbName}</span>
            </div>
            <div className="status-row">
              <span className="status-label">Status</span>
              <span className="status-value status-online">
                <span className="status-dot"></span> {dbStatus}
              </span>
            </div>
            <div className="status-row">
              <span className="status-label">Engine</span>
              <span className="status-value">Neo4j Bolt v5</span>
            </div>
          </div>
        </div>

        {/* Database Statistics */}
        <div className="sidebar-section system-stats">
          <h4 className="section-label">Dataset Statistics</h4>
          <div className="stats-grid">
            <div className="stat-pill">
              <span className="stat-count">{people.length || 4}</span>
              <span className="stat-title">Profiles</span>
            </div>
            <div className="stat-pill">
              <span className="stat-count">{jobs.length || 4}</span>
              <span className="stat-title">Roles</span>
            </div>
            <div className="stat-pill">
              <span className="stat-count">11</span>
              <span className="stat-title">Resources</span>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="dashboard-main">
        {initialError ? (
          <div className="error-hero">
            <svg
              className="error-hero-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0zM12 9v4M12 17h.01" />
            </svg>
            <h3>Service Connection Refused</h3>
            <p>
              The frontend application is unable to reach the Wexa backend
              service on <code>http://localhost:8080</code>. Verify the Spring
              Boot microservice is compiled and actively running.
            </p>
            <button className="btn btn-primary" onClick={loadInitialData}>
              Retry Core Handshake
            </button>
          </div>
        ) : !targetPerson || !targetJob ? (
          <div className="welcome-hero">
            <div className="welcome-graphic">
              {/* Dynamic decorative flow nodes */}
              <svg
                className="graphic-svg"
                viewBox="0 0 200 100"
                fill="none"
                stroke="currentColor"
              >
                <circle cx="30" cy="50" r="8" className="node start-node" />
                <line x1="38" y1="50" x2="92" y2="50" className="flow-line" />
                <polygon points="90,47 97,50 90,53" className="flow-arrow" />
                <circle cx="105" cy="50" r="10" className="node mid-node" />
                <line x1="115" y1="50" x2="162" y2="50" className="flow-line" />
                <polygon points="160,47 167,50 160,53" className="flow-arrow" />
                <circle cx="175" cy="50" r="8" className="node end-node" />
              </svg>
            </div>
            <h2>Career Navigation Graph Engine</h2>
            <p>
              Select an employee profile and a target professional role from the
              configuration panel on the left sidebar to execute the path
              analysis. The engine will determine necessary skill gaps and map
              the optimal learning sequences.
            </p>
          </div>
        ) : (
          <div className="dashboard-workspace">
            {/* Context Header */}
            <header className="workspace-header">
              <div className="header-meta">
                <span className="meta-breadcrumb">
                  Career path analysis for
                </span>
                <h1 className="active-target-title">
                  {targetPerson} <span>➔</span> {targetJob}
                </h1>
              </div>
            </header>

            {/* Main panels layout */}
            <div className="workspace-grid">
              <section className="workspace-panel">
                <div className="panel-header-bar">
                  <div className="panel-header-info">
                    <h4>Required Skill Gaps</h4>
                    <span>
                      Missing capabilities needed for the target job role
                    </span>
                  </div>
                </div>
                <div className="panel-body">
                  <SkillGapPanel
                    loading={loadingGap}
                    error={errorGap}
                    data={gapData}
                    targetPerson={targetPerson}
                    targetJob={targetJob}
                    onRetry={handleRetryGap}
                  />
                </div>
              </section>

              <section className="workspace-panel">
                <div className="panel-header-bar">
                  <div className="panel-header-info">
                    <h4>Optimal Learning Paths</h4>
                    <span>
                      Shortest path routing from candidate profile to target
                      skills
                    </span>
                  </div>
                </div>
                <div className="panel-body">
                  <LearningPathPanel
                    loading={loadingPath}
                    error={errorPath}
                    data={pathData}
                    targetPerson={targetPerson}
                    targetJob={targetJob}
                    onRetry={handleRetryPath}
                  />
                </div>
              </section>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
