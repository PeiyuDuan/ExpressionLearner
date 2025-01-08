package com.example.backend.utils;

import org.json.JSONObject;
import org.json.JSONException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonFixUtil {

    private static final Logger log = LogManager.getLogger(JsonFixUtil.class);

    /**
     * Attempts to repair a potentially malformed JSON string and return it as a JSONObject.
     *
     * @param input The input JSON string.
     * @return A valid JSONObject or throws JSONException if it cannot be repaired.
     * @throws JSONException Thrown if the JSON cannot be repaired or parsed.
     */
    public static JSONObject repairAndParseJson(String input) throws JSONException {
        String cleanedInput = input.trim();

        try {
            // Try parsing the input directly as JSON
            return new JSONObject(cleanedInput);
        } catch (JSONException e) {
            log.info("Initial JSON parsing failed, attempting repair. Input: {}", cleanedInput);
        }

        // Perform additional sanitization steps
        cleanedInput = cleanedInput
                .replace("{{", "{")
                .replace("}}", "}")
                .replace("\"[{", "[{")
                .replace("}]\"", "}]")
                .replace("\\", " ")
                .replace("\\n", " ")
                .replace("\n", " ")
                .replace("\r", "")
                .trim();

        // Remove JSON Markdown Frame
        if (cleanedInput.startsWith("```")) {
            cleanedInput = cleanedInput.substring(cleanedInput.indexOf("```") + 3).trim();
        }
        if (cleanedInput.startsWith("```json")) {
            cleanedInput = cleanedInput.substring("```json".length()).trim();
        }
        if (cleanedInput.endsWith("```")) {
            cleanedInput = cleanedInput.substring(0, cleanedInput.lastIndexOf("```")).trim();
        }

        // Attempt to extract the JSON part from input
        int startIdx = cleanedInput.indexOf('{');
        int endIdx = cleanedInput.lastIndexOf('}');

        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            cleanedInput = cleanedInput.substring(startIdx, endIdx + 1);
        } else {
            throw new JSONException("Unable to locate JSON boundaries in input.");
        }

        try {
            // Try parsing the repaired JSON
            return new JSONObject(cleanedInput);
        } catch (JSONException e) {
            log.error("Error repairing and parsing JSON. Cleaned input: {}", cleanedInput, e);
            throw new JSONException("Repaired JSON parsing failed.");
        }
    }
}
