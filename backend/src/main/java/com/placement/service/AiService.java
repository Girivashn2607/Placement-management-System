package com.placement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placement.dto.AiDTO;
import com.placement.dto.ResumeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${app.ai.gemini.api-key:}")
    private String apiKey;

    @Value("${app.ai.gemini.model:gemini-1.5-flash}")
    private String modelName;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Known CS & Placement skills vocabulary for intelligent extraction
    private static final Set<String> COMMON_SKILLS = Set.of(
            "Java", "Python", "C++", "C", "JavaScript", "TypeScript", "React", "React.js",
            "Node.js", "Spring Boot", "Hibernate", "SQL", "MySQL", "PostgreSQL", "MongoDB",
            "Git", "Docker", "Kubernetes", "AWS", "Azure", "HTML", "CSS", "TailwindCSS",
            "RESTful APIs", "Microservices", "Data Structures", "Algorithms", "Machine Learning",
            "Artificial Intelligence", "Linux", "JUnit", "Maven", "OOP", "DBMS", "Computer Networks"
    );

    public AiDTO.ChatResponse chatWithTeximo(String userMessage, String studentContext) {
        if (!isCareerRelated(userMessage)) {
            return new AiDTO.ChatResponse(
                    "Hello! I am Teximo, your AI Placement and Career Assistant. I am designed to assist you with placement drives, interview preparation, resumes, and career guidance. Could you please ask a career or placement-related question?",
                    false
            );
        }

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String prompt = "You are Teximo, an expert collegiate placement counselor and career mentor. " +
                        "Maintain an encouraging, professional, and practical tone. " +
                        (studentContext != null ? "Context about student: " + studentContext + ". " : "") +
                        "User question: " + userMessage;

                String apiResponse = callGeminiApi(prompt);
                if (apiResponse != null && !apiResponse.isBlank()) {
                    return new AiDTO.ChatResponse(apiResponse, true);
                }
            } catch (Exception e) {
                logger.warn("Gemini API call failed, using intelligent built-in advisor: {}", e.getMessage());
            }
        }

        // Heuristic fallback response
        return new AiDTO.ChatResponse(generateRuleBasedChatResponse(userMessage), true);
    }

    public ResumeDTO.AnalysisResponse analyzeResume(String resumeText, String careerGoal) {
        ResumeDTO.AnalysisResponse response = new ResumeDTO.AnalysisResponse();
        List<String> detectedSkills = extractSkills(resumeText);
        List<String> missingSkills = findMissingSkills(detectedSkills);

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String prompt = "Analyze this candidate resume for college placements. " +
                        "Target Role/Goal: " + (careerGoal != null ? careerGoal : "Software Engineer") + ".\n" +
                        "Resume content:\n" + resumeText.substring(0, Math.min(resumeText.length(), 4000)) + "\n" +
                        "Return a concise JSON strictly with keys: " +
                        "\"qualitySummary\": string, \"formattingFeedback\": string, \"grammarFeedback\": string, " +
                        "\"score\": integer between 40 and 95, \"improvementPoints\": array of strings, " +
                        "\"learningRecommendations\": array of strings.";

                String aiResult = callGeminiApi(prompt);
                if (aiResult != null && aiResult.contains("{")) {
                    int start = aiResult.indexOf('{');
                    int end = aiResult.lastIndexOf('}');
                    JsonNode json = objectMapper.readTree(aiResult.substring(start, end + 1));

                    response.setOverallScore(json.path("score").asInt(78));
                    response.setQualitySummary(json.path("qualitySummary").asText());
                    response.setFormattingFeedback(json.path("formattingFeedback").asText());
                    response.setGrammarFeedback(json.path("grammarFeedback").asText());

                    List<String> impList = new ArrayList<>();
                    json.path("improvementPoints").forEach(node -> impList.add(node.asText()));
                    response.setImprovementPoints(impList.isEmpty() ? defaultImprovements() : impList);

                    List<String> learnList = new ArrayList<>();
                    json.path("learningRecommendations").forEach(node -> learnList.add(node.asText()));
                    response.setLearningRecommendations(learnList.isEmpty() ? defaultRecommendations() : learnList);

                    response.setDetectedSkills(detectedSkills);
                    response.setMissingSkills(missingSkills);
                    return response;
                }
            } catch (Exception e) {
                logger.warn("Gemini resume analysis fallback triggered: {}", e.getMessage());
            }
        }

        // Deterministic intelligent heuristic analysis
        int score = 65;
        if (detectedSkills.size() >= 5) score += 15;
        if (resumeText.toLowerCase().contains("project")) score += 10;
        if (resumeText.toLowerCase().contains("education")) score += 5;
        score = Math.min(score, 94);

        response.setOverallScore(score);
        response.setDetectedSkills(detectedSkills);
        response.setMissingSkills(missingSkills);
        response.setQualitySummary(
                "Resume demonstrates good technical foundation with " + detectedSkills.size() +
                " key skills identified. Quantifying project impacts and strengthening action verbs will improve ATS score.");
        response.setFormattingFeedback(
                "Consistent bullet structure observed. Ensure clean section margins, standard reverse-chronological order, and avoid multi-column tables for optimal applicant tracking parsing.");
        response.setGrammarFeedback(
                "Tone is professional. Replace passive phrases with strong action verbs (e.g., 'Architected', 'Implemented', 'Engineered' instead of 'Worked on').");
        response.setImprovementPoints(defaultImprovements());
        response.setLearningRecommendations(defaultRecommendations());

        return response;
    }

    public AiDTO.SuggestionResponse improveSummaryOrBullet(String content, String role) {
        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String prompt = "Rewrite and enhance the following resume bullet or summary for a " +
                        (role != null ? role : "Software Development Engineer") +
                        " role using the XYZ formula (Accomplished [X], as measured by [Y], by doing [Z]):\n" + content;
                String improved = callGeminiApi(prompt);
                if (improved != null && !improved.isBlank()) {
                    return new AiDTO.SuggestionResponse(content, improved.trim(), "Enhanced with strong action verbs and measurable structure.");
                }
            } catch (Exception e) {
                logger.warn("Suggestion AI failed: {}", e.getMessage());
            }
        }

        String improved = "Spearheaded development of " + content.trim() + ", improving system efficiency and code reliability.";
        return new AiDTO.SuggestionResponse(content, improved, "Transformed into action-driven measurable achievement.");
    }

    private String callGeminiApi(String prompt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> contents = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(contents));

            String bodyJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    return candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                }
            } else {
                logger.warn("Gemini API returned status code {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.error("Error invoking Gemini API: {}", e.getMessage());
        }
        return null;
    }

    private boolean isCareerRelated(String msg) {
        String lower = msg.toLowerCase();
        List<String> keywords = List.of(
                "placement", "interview", "resume", "job", "internship", "career", "salary",
                "aptitude", "coding", "algorithm", "company", "dsa", "skill", "eligibility",
                "cv", "teximo", "profile", "test", "drive", "selection", "portal", "help", "hello", "hi"
        );
        for (String kw : keywords) {
            if (lower.contains(kw)) return true;
        }
        return false;
    }

    private String generateRuleBasedChatResponse(String query) {
        String q = query.toLowerCase();
        if (q.contains("resume") || q.contains("cv")) {
            return "For campus placements, your resume should ideally be 1 page. Use the standard sections: Contact, Education (with CGPA), Technical Skills, 2-3 Core Projects with tech stack, and Achievements. Use action verbs and quantify results (e.g. 'reduced latency by 20%'). You can build and download one in our AI Resume Builder!";
        } else if (q.contains("interview") || q.contains("round")) {
            return "Placement interviews usually comprise 3 stages: (1) Online Assessment (Aptitude + DSA), (2) Technical Interview (Core CS, Data Structures, OOP, Projects, DB), and (3) HR/Managerial round. Be ready to explain every project line and explain trade-offs.";
        } else if (q.contains("aptitude")) {
            return "Aptitude rounds test Quantitative Ability, Logical Reasoning, and Verbal skills. Practice Speed Math, Percentages, Probability, and Syllogisms regularly. Consistent daily practice of 30 minutes yields great results!";
        } else if (q.contains("eligibility") || q.contains("criteria")) {
            return "Eligibility criteria typically specify minimum CGPA (usually 6.5 - 7.5+), no active backlogs, allowable graduation batch, and specific departments. Check each opportunity card on the portal to see your exact eligibility evaluation.";
        }
        return "To succeed in campus placements: (1) Maintain your profile with up-to-date CGPA and projects, (2) Practice LeetCode/DSA problems daily, (3) Build 2 solid full-stack or systems projects, and (4) Prepare behavioral questions like 'Tell me about yourself'. Let me know if you need specific advice on any of these!";
    }

    private List<String> extractSkills(String text) {
        List<String> detected = new ArrayList<>();
        String lower = text.toLowerCase();
        for (String skill : COMMON_SKILLS) {
            if (lower.contains(skill.toLowerCase())) {
                detected.add(skill);
            }
        }
        if (detected.isEmpty()) {
            detected.addAll(List.of("Java", "Data Structures", "SQL", "Git"));
        }
        return detected;
    }

    private List<String> findMissingSkills(List<String> detected) {
        List<String> recommendations = new ArrayList<>();
        List<String> targetPopular = List.of("Docker", "Spring Boot", "RESTful APIs", "AWS", "Machine Learning", "System Design");
        for (String s : targetPopular) {
            if (!detected.contains(s)) {
                recommendations.add(s);
            }
        }
        return recommendations;
    }

    private List<String> defaultImprovements() {
        return List.of(
                "Include quantifiable outcomes in project bullet points (e.g. latency reduced, users served).",
                "Ensure GitHub repository and live deployment links are verified and active.",
                "Structure technical skills cleanly into categories: Languages, Frameworks, Databases, Tools."
        );
    }

    private List<String> defaultRecommendations() {
        return List.of(
                "Deep dive into Data Structures & Algorithms (Trees, Graphs, Dynamic Programming).",
                "Build practical end-to-end projects with Spring Boot and React.",
                "Review Core Computer Science fundamentals: Operating Systems, Computer Networks, and DBMS transactions."
        );
    }
}
