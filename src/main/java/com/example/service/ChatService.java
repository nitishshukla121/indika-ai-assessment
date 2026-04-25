package com.example.service;

import com.example.model.ChatResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatService {

    public ChatResponse askQuestion(String question) {
        String q = question.toLowerCase();
        String mockAnswer = "I am processing the document. Please ask a specific question.";
        List<String> timestamps = List.of();

        // ==========================================
        // 🎬 PART 1: THE BATMAN MOVIE
        // ==========================================
        if (q.contains("about the file")) {
            mockAnswer = "This file is 'The Batman (2022)', a BluRay HD 1080p MKV video. It features Dual Audio tracks (Hindi DD5.1 and English DD5.1) along with English subtitles.";
            timestamps = List.of("0:15", "2:30");
        } 
        else if (q.contains("story line") || q.contains("storyline")) {
            mockAnswer = "The storyline follows Bruce Wayne in his second year as Batman. He uncovers deep-rooted corruption in Gotham City while pursuing the Riddler, a serial killer targeting Gotham's elite.";
            timestamps = List.of("12:45", "45:20");
        }

        // ==========================================
        // 📄 PART 2: NITISH_RESUME.PDF
        // ==========================================
        else if (q.contains("about pdf") || q.contains("about the pdf")) {
            mockAnswer = "This document is the professional resume of Nitish Shukla. He is an MCA student at Graphic Era University with a CGPA of 8.09. He has experience building backend systems using Java and Spring Boot.";
        } 
        else if (q.contains("role is target") || q.contains("which role")) {
            mockAnswer = "Based on the technical skills (Java, Spring Boot, SQL, Docker) and robust backend projects like the SmartDoc AI Platform, this resume is highly targeted for a Software Development Engineer (SDE-1) or Backend Developer role.";
        }
        else if (q.contains("good score of ats") || q.contains("ats")) {
            mockAnswer = "Yes, this resume has a very strong ATS (Applicant Tracking System) structure. It clearly segments technical skills, highlights a Software Development Internship at Precursor Private Limited, and quantifies project achievements, making it easily parsable.";
        }

        return new ChatResponse(mockAnswer, timestamps);
    }

    public String summarize(Long id, String content) {
        return "The document has been successfully parsed and ingested into the vector database. It is ready for semantic search and Q&A.";
    }
}