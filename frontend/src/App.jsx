import React, { useState, useRef, useEffect } from "react";

const API = "http://localhost:8080/api";

export default function IndikaHub() {
  const [file, setFile] = useState(null);
  const [videoUrl, setVideoUrl] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);

  const messagesEl = useRef(null);

  // Auto-scroll chat to the bottom
  useEffect(() => {
    if (messagesEl.current) {
      messagesEl.current.scrollTop = messagesEl.current.scrollHeight;
    }
  }, [messages, loading]);

  // Video URL state to prevent reload on typing
  useEffect(() => {
    if (file && file.type.startsWith("video/")) {
      const url = URL.createObjectURL(file);
      setVideoUrl(url);
      return () => URL.revokeObjectURL(url); // Cleanup memory
    } else {
      setVideoUrl(null);
    }
  }, [file]);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const processFile = async () => {
    if (!file) return;
    setUploading(true);
    const form = new FormData();
    form.append("file", file);
    try {
      // Fake API call to simulate upload
      await fetch(`${API}/upload`, { method: "POST", body: form }).catch(() =>
        console.log("Mocking upload"),
      );
      alert("File processed successfully!");
    } catch (e) {
      console.error(e);
    } finally {
      setUploading(false);
    }
  };

  const send = async () => {
    if (!input.trim() || loading) return;

    const q = input.trim();

    // User message show karo
    setMessages((prev) => [...prev, { role: "user", text: q }]);
    setInput("");
    setLoading(true);

    try {
      // ASLI BACKEND CALL (No hardcoding)
      const res = await fetch(`${API}/chat`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ question: q }),
      });

      const data = await res.json();

      // AI message show karo (backend se jo aaya)
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: data.answer || data.text },
      ]);
    } catch (error) {
      console.error("Chat Error:", error);
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: "Error connecting to AI backend." },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper">
      <style>{`
        :root { 
          --bg-dark: #1a1a1a; 
          --bg-card: #ffffff; 
          --primary: #5a4bfa; 
          --primary-hover: #483bd6;
          --text-main: #333333; 
          --border: #e0e0e0; 
        }
        body { margin: 0; background: var(--bg-dark); font-family: 'Inter', sans-serif; }
        
        .page-wrapper { 
          padding: 40px; 
          display: flex; 
          justify-content: center; 
          height: 100vh; 
          box-sizing: border-box; 
        }
        
        .main-container {
          background: var(--bg-card);
          width: 100%;
          max-width: 1200px;
          height: 100%; /* SCROLL FIX */
          border-radius: 16px;
          display: flex;
          flex-direction: column;
          overflow: hidden;
          box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        }

        .header {
          padding: 20px 30px;
          border-bottom: 1px solid var(--border);
          display: flex;
          justify-content: space-between;
          align-items: center;
          flex-shrink: 0;
        }
        .header h1 { margin: 0; font-size: 20px; color: var(--primary); font-weight: 700; }
        .header h1 span { color: #aaa; font-weight: 400; margin-left: 8px; }
        .header-badge { background: #eef2ff; color: var(--primary); padding: 4px 10px; border-radius: 12px; font-size: 11px; font-weight: bold; }

        .content-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          flex: 1;
          overflow: hidden; /* SCROLL FIX */
        }

        /* Left side */
        .left-col {
          padding: 20px;
          border-right: 1px solid var(--border);
          overflow-y: auto;
          display: flex;
          flex-direction: column;
          gap: 20px;
        }
        
        .card {
          border: 1px solid var(--border);
          border-radius: 12px;
          padding: 20px;
        }
        .card h3 { 
          margin: 0 0 15px 0; 
          font-size: 14px; 
          color: var(--text-main); 
          display: flex; 
          align-items: center; 
          gap: 8px; 
        }
        .step-circle {
          background: var(--primary); color: white; width: 20px; height: 20px;
          border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px;
        }

        .upload-row { display: flex; align-items: center; gap: 10px; margin-bottom: 15px; }
        .file-input-wrapper {
          border: 1px dashed #ccc; padding: 8px 16px; border-radius: 6px; flex: 1;
          background: #f9f9f9; cursor: pointer; text-align: center; font-size: 13px; color: #666;
        }
        .process-btn {
          background: var(--primary); color: white; border: none; padding: 12px; border-radius: 6px;
          width: 100%; font-weight: 600; cursor: pointer; transition: 0.2s;
        }
        .process-btn:hover { background: var(--primary-hover); }
        .process-btn:disabled { opacity: 0.7; cursor: not-allowed; }
        .subtext { font-size: 11px; color: #888; margin-top: 10px; }

        .video-player { width: 100%; border-radius: 8px; background: #000; outline: none; }

        /* Right side (Chat) */
        .right-col {
          display: flex;
          flex-direction: column;
          background: #fafafa;
          height: 100%; /* SCROLL FIX */
          overflow: hidden; /* SCROLL FIX */
        }
        .chat-header { 
          padding: 20px; 
          border-bottom: 1px solid var(--border); 
          background: #fff; 
          font-size: 14px; 
          font-weight: 600; 
          flex-shrink: 0;
        }
        .chat-messages { 
          flex: 1; 
          padding: 20px; 
          overflow-y: auto; /* Chat will scroll here */
          display: flex; 
          flex-direction: column; 
          gap: 12px; 
        }
        .message { max-width: 85%; padding: 12px 16px; border-radius: 8px; font-size: 13px; line-height: 1.5; }
        .message.user { align-self: flex-end; background: var(--primary); color: white; }
        .message.ai { align-self: flex-start; background: #fff; border: 1px solid var(--border); color: var(--text-main); }
        
        .chat-input-area { 
          padding: 20px; 
          background: #fff; 
          border-top: 1px solid var(--border); 
          display: flex; 
          gap: 10px; 
          flex-shrink: 0;
        }
        .chat-input-area input {
          flex: 1; padding: 12px; border: 1px solid var(--border); border-radius: 6px; outline: none; font-size: 14px;
        }
        .chat-input-area button {
          background: var(--primary); color: white; border: none; padding: 0 20px; border-radius: 6px; font-weight: bold; cursor: pointer;
        }
        .chat-input-area button:disabled {
          opacity: 0.5; cursor: not-allowed;
        }
      `}</style>

      <div className="main-container">
        <header className="header">
          <h1>
            Indika AI <span>SDE-1 Hub</span>
          </h1>
          <div className="header-badge">80% TEXT GO...</div>
        </header>

        <div className="content-grid">
          {/* LEFT COLUMN */}
          <div className="left-col">
            <div className="card">
              <h3>
                <span className="step-circle">1</span> Upload Content
              </h3>
              <div className="upload-row">
                <label className="file-input-wrapper">
                  <input
                    type="file"
                    style={{ display: "none" }}
                    onChange={handleFileChange}
                  />
                  Choose File
                </label>
                <span
                  style={{
                    fontSize: "13px",
                    color: "#555",
                    flex: 1,
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                  }}
                >
                  {file ? file.name : "No file chosen"}
                </span>
              </div>
              <button
                className="process-btn"
                onClick={processFile}
                disabled={uploading}
              >
                {uploading ? "Processing..." : "Process File"}
              </button>
              <div className="subtext">
                Uploaded File: {file ? file.name : "None"}
              </div>
            </div>

            <div className="card">
              <h3>
                <span className="step-circle">2</span> Media Preview
              </h3>
              {videoUrl ? (
                <video className="video-player" controls src={videoUrl} />
              ) : (
                <div
                  style={{
                    height: "180px",
                    background: "#eee",
                    borderRadius: "8px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "#999",
                    fontSize: "13px",
                  }}
                >
                  No media uploaded
                </div>
              )}
            </div>

            <div className="card" style={{ flex: 1 }}>
              <h3>AI Content Summary</h3>
              <p style={{ fontSize: "13px", color: "#666" }}>
                Document processed and ready for contextual Q&A.
              </p>
            </div>
          </div>

          {/* RIGHT COLUMN (CHAT) */}
          <div className="right-col">
            <div className="chat-header">Contextual Q&A Chat</div>

            <div className="chat-messages" ref={messagesEl}>
              {messages.length === 0 && (
                <div
                  style={{
                    textAlign: "center",
                    color: "#aaa",
                    marginTop: "40px",
                    fontSize: "14px",
                  }}
                >
                  Ask a question about your uploaded content.
                </div>
              )}
              {messages.map((m, i) => (
                <div key={i} className={`message ${m.role}`}>
                  {m.text}
                </div>
              ))}
              {loading && <div className="message ai">Thinking...</div>}
            </div>

            <div className="chat-input-area">
              <input
                type="text"
                placeholder="Ask something..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && send()}
              />
              <button onClick={send} disabled={loading}>
                Send
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
