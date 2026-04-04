// src/api/Code_Service.js

export const reviewCode = async (code, language) => {
  try {
    const res = await fetch(`http://localhost:8082/api/review`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code, language })
    });

    return await res.json(); // assumes backend returns JSON { message, score }
  } catch (err) {
    return { message: "Error connecting to backend", score: null };
  }
};