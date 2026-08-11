/**
 * EmptyState renders a user-friendly message when no results are found.
 *
 * @param {{message: string}} props
 */
export default function EmptyState({
  message = "No records match this query.",
}) {
  return (
    <div className="state-container empty-state">
      <div className="empty-icon">✓</div>
      <p className="state-text">{message}</p>
    </div>
  );
}
