import { useState } from "react";
import axios from "axios";

function App() {
  const [file, setFile] = useState(null);
  const [uploadStatus, setUploadStatus] = useState("");
  const [chatInput, setChatInput] = useState("");
  const [chatHistory, setChatHistory] = useState([]);

  const API_BASE = "http://localhost:8080/api";

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      alert("Please select a file first!");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      setUploadStatus("Uploading and processing... This might take a minute.");
      const response = await axios.post(`${API_BASE}/upload`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setUploadStatus(`Success! Uploaded: ${response.data.fileName}`);
    } catch (error) {
      console.error(error);
      setUploadStatus("Upload failed. Is the backend running?");
    }
  };

  const handleAskQuestion = async () => {
    if (!chatInput.trim()) return;

    const newHistory = [...chatHistory, { role: "user", text: chatInput }];
    setChatHistory(newHistory);
    setChatInput("");

    try {
      const response = await axios.post(`${API_BASE}/chat`, {
        question: chatInput,
      });
      setChatHistory([
        ...newHistory,
        {
          role: "ai",
          text: response.data.answer,
          timestamps: response.data.relevantTimestamps,
        },
      ]);
    } catch (error) {
      console.error(error);
      setChatHistory([
        ...newHistory,
        { role: "ai", text: "Error connecting to AI." },
      ]);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-8 font-sans text-gray-800">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-4xl font-bold text-blue-600 mb-8">
          Indika AI Intelligence Hub
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="bg-white p-6 rounded-lg shadow-md border border-gray-100">
            <h2 className="text-xl font-semibold mb-4">
              1. Upload Document or Media
            </h2>
            <input
              type="file"
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 mb-4"
            />
            <button
              onClick={handleUpload}
              className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition"
            >
              Upload & Process
            </button>
            {uploadStatus && (
              <div className="mt-4 p-3 bg-gray-50 text-sm rounded border">
                {uploadStatus}
              </div>
            )}
          </div>

          <div className="md:col-span-2 bg-white p-6 rounded-lg shadow-md border border-gray-100 flex flex-col h-[600px]">
            <h2 className="text-xl font-semibold mb-4">2. Ask the AI</h2>
            <div className="flex-1 overflow-y-auto mb-4 p-4 border rounded bg-gray-50">
              {chatHistory.length === 0 ? (
                <p className="text-gray-400 text-center mt-20">
                  Upload a document and ask a question to start.
                </p>
              ) : (
                chatHistory.map((msg, index) => (
                  <div
                    key={index}
                    className={`mb-4 ${msg.role === "user" ? "text-right" : "text-left"}`}
                  >
                    <div
                      className={`inline-block p-3 rounded-lg max-w-[80%] ${msg.role === "user" ? "bg-blue-600 text-white" : "bg-white border shadow-sm"}`}
                    >
                      <p>{msg.text}</p>
                      {msg.timestamps && msg.timestamps.length > 0 && (
                        <div className="mt-2 text-xs text-blue-500 font-mono">
                          Timestamps: {msg.timestamps.join(", ")}
                        </div>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
            <div className="flex gap-2">
              <input
                type="text"
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleAskQuestion()}
                placeholder="Ask about your uploaded files..."
                className="flex-1 border p-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                onClick={handleAskQuestion}
                className="bg-green-600 text-white px-6 py-2 rounded-md hover:bg-green-700 transition"
              >
                Send
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
