package com.lolplane.fudge.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for validating templates to check for potentially malicious content.
 */
public class TemplateValidator {

    // List of potentially dangerous patterns in templates
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
        // JavaScript injection patterns
        Pattern.compile("<script.*?>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE), // onclick, onload, etc.

        // CSS injection patterns
        Pattern.compile("<style.*?>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),

        // Iframe injection
        Pattern.compile("<iframe.*?>", Pattern.CASE_INSENSITIVE),

        // Object/embed injection
        Pattern.compile("<object.*?>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed.*?>", Pattern.CASE_INSENSITIVE),

        // Mustache template injection (attempting to access Java internals)
        Pattern.compile("\\{\\{\\s*[^}]*\\.class", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.getClass", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.forName", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.newInstance", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.getMethod", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.invoke", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.getDeclaredField", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\{\\{\\s*[^}]*\\.setAccessible", Pattern.CASE_INSENSITIVE)
    );

    /**
     * Validates a template to check for potentially malicious content.
     *
     * @param templateContent The template content as a string
     * @return true if the template is safe, false otherwise
     */
    public static boolean isTemplateSafe(String templateContent) {
        if (templateContent == null || templateContent.isEmpty()) {
            return false;
        }

        // Check for dangerous patterns
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(templateContent).find()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates a template to check for potentially malicious content.
     *
     * @param templateStream The template content as an input stream
     * @return true if the template is safe, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public static boolean isTemplateSafe(InputStream templateStream) throws IOException {
        if (templateStream == null) {
            return false;
        }

        // Read the template content
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(templateStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return isTemplateSafe(content.toString());
    }
}
