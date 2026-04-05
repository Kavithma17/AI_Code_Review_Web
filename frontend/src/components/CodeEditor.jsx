import { useState } from "react";
import { reviewCode } from "../api/Code_Service";
import "./CodeEditor.css";
import ReactMarkdown from 'react-markdown';

export default function CodeEditor() {
  const [code, setCode] = useState("");
  const [language, setLanguage] = useState("Java");
  
  // Updated state to match Spring Boot response
  const [aiReview, setAiReview] = useState("");
  const [staticAnalysis, setStaticAnalysis] = useState("");
  const [loading, setLoading] = useState(false);

  const handleReview = async () => {
    setLoading(true);
    setAiReview("");
    setStaticAnalysis("");

    try {
      const data = await reviewCode(code, language);
      
      // Map to the correct fields from ReviewResponse.java
      setAiReview(data.aiReview);
      setStaticAnalysis(data.staticAnalysis);
      
    } catch (error) {
      setAiReview("Something went wrong connecting to the server.");
      setStaticAnalysis("");
    }

    setLoading(false);
  };

  return (
    <div className="editor-container">
      <h2>AI Code Review</h2>

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
        disabled={!code || loading}
        className="editor-button"
      >
        {loading ? "Reviewing..." : "Review Code"}
      </button>

      {/* Only show the output box if we have results */}
      {(aiReview || staticAnalysis) && (
        <div className="editor-output" style={{ marginTop: '20px', textAlign: 'left' }}>
  
  <h3 style={{ color: '#4da6ff' }}>🧠 AI Insights:</h3>
  
  {/* 2. Wrap the response in ReactMarkdown */}
  <div className="markdown-body" style={{ lineHeight: '1.6', fontSize: '15px' }}>
    <ReactMarkdown>{aiReview}</ReactMarkdown>
  </div>
  
  <hr style={{ margin: '20px 0', borderColor: '#444' }}/>
  
  <h3 style={{ color: '#ff6b6b' }}>🛠️ Static Analysis:</h3>
  <pre style={{ whiteSpace: 'pre-wrap' }}>{staticAnalysis}</pre>
  
</div>
      )}
    </div>
  );
}