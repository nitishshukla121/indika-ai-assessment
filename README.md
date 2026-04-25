# 🚀 Indika AI - SDE-1 Programming Assignment

[cite_start]A full-stack AI-Powered Document & Multimedia Q&A Web Application built to fulfill the Indika AI SDE-1 assignment requirements[cite: 39, 40].

## 🎯 Objective Fulfillment
This application successfully implements all core requirements:
* [cite_start]**Multi-format Upload:** Supports uploading PDF documents, audio, and video files[cite: 43].
* [cite_start]**AI Chatbot:** Context-aware Q&A against the uploaded files using an LLM-powered RAG pipeline[cite: 44].
* [cite_start]**Auto-Summarization:** Instantly generates summaries for uploaded content[cite: 45].
* [cite_start]**Media Timestamps & Play Button:** Extracts timestamps from media files and provides a "Play" button in the chat to jump to the exact relevant portion of the video/audio[cite: 46, 63, 64].

## 🛠️ Technology Stack
* [cite_start]**Backend:** Java 17, Spring Boot 3.4.1 [cite: 50]
* [cite_start]**Frontend:** React.js, Vite, pure CSS [cite: 59]
* [cite_start]**AI & LLM Integration:** Spring AI, Ollama (`llama3.2:1b` & `nomic-embed-text`) [cite: 51]
* [cite_start]**Database & Vector Search:** In-Memory H2 (Metadata & Vector Store for semantic search) [cite: 54, 69]
* [cite_start]**Infrastructure:** Docker, Docker Compose, GitHub Actions (CI/CD) [cite: 56, 57, 66]

## 🏗️ Architecture & Design Decisions
* [cite_start]**Local LLM vs Cloud APIs:** While the assignment suggested OpenAI/Whisper[cite: 51, 52], this solution implements a **100% Local AI Pipeline** using Ollama. This architectural choice eliminates API costs, ensures zero data-leakage (privacy-first), and demonstrates the ability to optimize heavy LLM workloads on constrained local hardware environments.
* [cite_start]**Vector Search:** Implemented using Spring AI's SimpleVectorStore, mapping chunks and embeddings for fast semantic retrieval[cite: 69].

## ⚙️ Setup & Running Instructions

### 1. Prerequisites
* Java 17 & Maven
* Node.js & npm
* [cite_start]Docker & Docker Compose [cite: 66]
* Ollama (with `llama3.2:1b` and `nomic-embed-text` pulled locally)

### 2. Run via Docker Compose (Recommended)
[cite_start]A `docker-compose.yml` is provided to spin up the application seamlessly[cite: 66].
```bash
docker-compose up --build -d

## 🎥 Live Demo
Watch the full walkthrough video here: [Live Walkthrough Video](https://1drv.ms/v/c/9b6baeebbb28a7e1/IQCq9HoDVis2RrfiNmjUT8mhASQssUOppv6kCP5rveo2exo?e=4S8Ozc)
