import { useState } from "react";
import { reviewCode } from "../api/Code_Service";


export default function CodeEditor() {
  const [code, setCode] = useState("");
  const [language, setLanguage] = useState("Java");
  const [response, setResponse] = useState("");
  const [score, setScore] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleReview = async () => {
    setLoading(true);
    const data = await reviewCode(code, language);
    setResponse(data.message);
    setScore(data.score);
    setLoading(false);
  };

  return (
    <div className="editor-container">
      <select
        value={language}
        onChange={(e) => setLanguage(e.target.value)}
        className="editor-select"
      >
        <option>Java</option>
        <option>Python</option>
        <option>JavaScript</option>
      </select>

      <textarea
        placeholder="Paste your code here..."
        value={code}
        onChange={(e) => setCode(e.target.value)}
        className="editor-textarea"
      />

      <button
        onClick={handleReview}
        disabled={!code}
        className="editor-button"
      >
        {loading ? "Reviewing..." : "Review Code"}
      </button>

      <div className="editor-output">
        <h3>Review Result:</h3>
        <pre>{response}</pre>
        {score !== null && <p>Score: {score}/10</p>}
      </div>
    </div>
  );
}