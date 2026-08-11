/**
 * LoadingState renders a clean, animated loading indicator.
 *
 * @param {{message: string}} props
 */
export default function LoadingState({ message = "Loading details..." }) {
  return (
    <div className="state-container loading-state">
      <div className="spinner"></div>
      <p className="state-text">{message}</p>
    </div>
  );
}
