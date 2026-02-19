package com.smarttask.config;

import org.springframework.context.annotation.Configuration;

/**
 * AI Configuration
 * 
 * Note: The AI service now uses a rule-based implementation that doesn't require
 * external AI API keys or Spring AI dependencies.
 * 
 * To enable Gemini AI integration in the future:
 * 1. Add the Spring AI Google GenAI starter dependency to pom.xml
 * 2. Configure the GEMINI_API_KEY in application.yml
 * 3. Update this configuration to inject GoogleAiChatModel
 */
@Configuration
public class AIConfig {
    // AIService is now a simple @Service bean with rule-based AI functionality
    // No external dependencies required
}
