package uk.ac.cf._5.group14.One_To_One.Chat;

/**
 * Simple DTO representing a chat prompt from the user.
 */
public record ChatRequest(String message, Boolean skipHistory) {}
