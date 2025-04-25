package com.lolplane.fudge.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for validating URLs to ensure secure handling of external resources.
 */
public class URLValidator {

    // List of allowed protocols
    private static final List<String> ALLOWED_PROTOCOLS = Arrays.asList("http", "https", "file");

    // List of allowed hosts for remote URLs (empty means all hosts are allowed)
    private static final List<String> ALLOWED_HOSTS = List.of();

    /**
     * Validates a URL to ensure it uses an allowed protocol and host.
     *
     * @param url The URL to validate
     * @return true if the URL is safe, false otherwise
     */
    public static boolean isURLSafe(URL url) {
        if (url == null) {
            return false;
        }

        // Check if the protocol is allowed
        String protocol = url.getProtocol();
        if (!ALLOWED_PROTOCOLS.contains(protocol)) {
            return false;
        }

        // For remote URLs, check if the host is allowed
        if ("http".equals(protocol) || "https".equals(protocol)) {
            String host = url.getHost();
            // If ALLOWED_HOSTS is empty, all hosts are allowed
            // Otherwise, check if the host is in the allowed list
            return ALLOWED_HOSTS.isEmpty() || ALLOWED_HOSTS.contains(host);
        }

        // For file URLs, additional checks could be added here
        // For example, checking if the file path is within a certain directory

        return true;
    }

    /**
     * Validates a URL string to ensure it uses an allowed protocol and host.
     *
     * @param urlString The URL string to validate
     * @return true if the URL is safe, false otherwise
     */
    public static boolean isURLSafe(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(urlString);
            return isURLSafe(url);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
