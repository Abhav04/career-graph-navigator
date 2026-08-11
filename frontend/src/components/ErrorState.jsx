/**
 * ErrorState renders a structured error card with retry support.
 *
 * @param {{error: Error|string, onRetry: function}} props
 */
export default function ErrorState({ error, onRetry }) {
  const errorMessage =
    error instanceof Error
      ? error.message
      : typeof error === "object" && error !== null
        ? error.error || JSON.stringify(error)
        : String(error);

  return (
    <div className="state-container error-state">
      <div className="error-icon">⚠️</div>
      <h3 className="error-title">Analysis Failed</h3>
      <p className="state-text">{errorMessage}</p>
      {onRetry && (
        <button className="btn btn-secondary retry-btn" onClick={onRetry}>
          Retry Query
        </button>
      )}
    </div>
  );
}
