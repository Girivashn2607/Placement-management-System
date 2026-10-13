package com.placement.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AiDTO {

    public static class ChatRequest {
        @NotBlank(message = "Message cannot be empty")
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ChatResponse {
        private String reply;
        private LocalDateTime timestamp = LocalDateTime.now();
        private boolean careerRelated = true;

        public ChatResponse() {}

        public ChatResponse(String reply, boolean careerRelated) {
            this.reply = reply;
            this.careerRelated = careerRelated;
            this.timestamp = LocalDateTime.now();
        }

        public String getReply() { return reply; }
        public void setReply(String reply) { this.reply = reply; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public boolean isCareerRelated() { return careerRelated; }
        public void setCareerRelated(boolean careerRelated) { this.careerRelated = careerRelated; }
    }

    public static class SuggestionRequest {
        @NotBlank(message = "Prompt content is required")
        private String content;

        private String targetRole;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getTargetRole() { return targetRole; }
        public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    }

    public static class SuggestionResponse {
        private String originalContent;
        private String improvedContent;
        private String reason;

        public SuggestionResponse() {}
        public SuggestionResponse(String originalContent, String improvedContent, String reason) {
            this.originalContent = originalContent;
            this.improvedContent = improvedContent;
            this.reason = reason;
        }

        public String getOriginalContent() { return originalContent; }
        public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }
        public String getImprovedContent() { return improvedContent; }
        public void setImprovedContent(String improvedContent) { this.improvedContent = improvedContent; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
