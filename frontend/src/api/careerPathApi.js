export const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";


/**
 * Helper to handle fetch responses and handle HTTP errors.
 *
 * @param {Response} response
 * @returns {Promise<any>}
 */
async function handleResponse(response) {
  if (!response.ok) {
    let errorDetail = "Server error occurred";
    try {
      const errJson = await response.json();
      errorDetail = errJson.detail || errJson.error || errorDetail;
    } catch {
      // ignore json parse error on raw error pages
    }
    throw new Error(errorDetail);
  }
  return response.json();
}

/**
 * Fetches all people names from the backend.
 *
 * @returns {Promise<string[]>}
 */
export async function fetchPeople() {
  const response = await fetch(`${API_BASE}/people`);
  return handleResponse(response);
}

/**
 * Fetches all job titles from the backend.
 *
 * @returns {Promise<string[]>}
 */
export async function fetchJobs() {
  const response = await fetch(`${API_BASE}/jobs`);
  return handleResponse(response);
}

/**
 * Fetches the skill gap analysis for a given person and job.
 *
 * @param {string} person
 * @param {string} job
 * @returns {Promise<any[]>}
 */
export async function fetchGapAnalysis(person, job) {
  const response = await fetch(
    `${API_BASE}/career-path/gap-analysis?person=${encodeURIComponent(
      person,
    )}&job=${encodeURIComponent(job)}`,
  );
  return handleResponse(response);
}

/**
 * Fetches the optimal learning path of prerequisite skills.
 *
 * @param {string} person
 * @param {string} job
 * @returns {Promise<any[]>}
 */
export async function fetchShortestPath(person, job) {
  const response = await fetch(
    `${API_BASE}/career-path/shortest-path?person=${encodeURIComponent(
      person,
    )}&job=${encodeURIComponent(job)}`,
  );
  return handleResponse(response);
}
