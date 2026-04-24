Indika AI Intelligence Hub
A production-grade, full-stack RAG (Retrieval-Augmented Generation) application designed to process and analyze diverse media types, including PDFs, audio, and video files. The platform provides intelligent insights, automated summaries, and context-aware chat capabilities with precise timestamp references for multimedia content.

🚀 Key Features
Multimedia Ingestion: Processes PDFs using Apache Tika and media files via OpenAI Whisper.

Speech-to-Text with Timestamps: Deep integration with Whisper-1 to extract transcriptions and segment-level timestamps for audio/video.

Intelligent RAG Pipeline: Uses OpenAI embeddings and Pinecone vector storage to retrieve relevant context for user queries.

Auto-Summarization: Instant generation of concise document summaries powered by LLMs.

High-Performance Caching: Redis-backed caching for chat responses and summaries to minimize latency and API costs.

Enterprise Grade Testing: 95%+ backend code coverage verified via JaCoCo and automated CI/CD pipelines.

🛠️ Tech Stack
Backend: Java 17, Spring Boot 3.4.1, Spring AI.

Frontend: React, Tailwind CSS, Vite.

Databases: PostgreSQL (Metadata), Redis (Cache), Pinecone (Vector Store).

AI Models: OpenAI GPT-3.5 Turbo, Whisper-1, and text-embedding-ada-002.

📋 Prerequisites
Docker and Docker Compose

Java 17 (JDK)

Node.js & npm

OpenAI API Key

Pinecone API Credentials

⚙️ Setup & Installation
1. Configure Environment
Ensure the following variables are available in your environment or application.properties:

OPENAI_API_KEY

PINECONE_API_KEY

PINECONE_ENV

PINECONE_PROJECT_ID

2. Launch Infrastructure
Boot up the required database and caching containers:

Bash
docker-compose up -d
3. Start Backend Server
Run the Spring Boot application from the root directory:

Bash
./mvnw spring-boot:run
The server will be available at http://localhost:8080.

4. Start Frontend UI
Navigate to the frontend directory and launch the development server:

Bash
cd frontend
npm install
npm run dev
The UI will be accessible at http://localhost:5173.

🧪 Testing & CI/CD
The project utilizes a robust testing strategy with Mockito and JUnit 5.
A GitHub Actions workflow is configured to automatically build the project, run tests, and generate JaCoCo coverage reports on every push.

To run tests locally:

Bash
./mvnw test jacoco:report
🏗️ Architecture
The system follows a standard RAG pattern:

Ingestion: Files are cleaned and transcribed into raw text.

Indexing: Text is split into chunks, embedded, and stored in Pinecone.

Retrieval: Similarity search finds the most relevant document fragments for a user's question.

Augmentation: Context is passed to GPT-3.5 Turbo with a custom system prompt to generate a grounded response.
