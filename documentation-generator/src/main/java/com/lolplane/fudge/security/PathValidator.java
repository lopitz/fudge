package com.lolplane.fudge.security;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.experimental.UtilityClass;

/**
 * Utility class for validating paths to prevent path traversal attacks.
 */
@UtilityClass
public class PathValidator {

    /**
     * Validates a path to prevent path traversal attacks.
     *
     * @param path The path to validate
     * @return true if the path is safe, false otherwise
     */
    public static boolean isPathSafe(String path) {
        if (path == null) {
            return false;
        }

        // Allow "." and ".." as standalone paths
        if (path.equals(".") || path.equals("..")) {
            return true;
        }

        // Check for path traversal sequences
        if (path.contains("..") || path.contains("./") || path.contains("/.")) {
            return false;
        }

        // Check for absolute paths
        var pathObj = Paths.get(path);
        if (pathObj.isAbsolute()) {
            // Absolute paths are allowed, but we need to make sure they don't contain
            // any path traversal sequences after normalization
            var normalizedPath = pathObj.normalize().toString();
            return normalizedPath.equals(path);
        }

        return true;
    }

    /**
     * Validates a path to prevent path traversal attacks.
     *
     * @param path The path to validate
     * @return true if the path is safe, false otherwise
     */
    public static boolean isPathSafe(Path path) {
        if (path == null) {
            return false;
        }

        // Normalize the path and check if it's the same as the original
        var normalizedPath = path.normalize();
        return normalizedPath.toString().equals(path.toString());
    }

    /**
     * Validates that a path is within a base directory to prevent path traversal attacks.
     *
     * @param basePath The base directory path
     * @param path     The path to validate
     * @return true if the path is within the base directory, false otherwise
     */
    public static boolean isPathWithinBase(Path basePath, Path path) {
        if (basePath == null || path == null) {
            return false;
        }

        // Normalize both paths
        var normalizedBasePath = basePath.normalize();
        var normalizedPath = path.normalize();

        // Check if the normalized path starts with the normalized base path
        return normalizedPath.startsWith(normalizedBasePath);
    }
}
