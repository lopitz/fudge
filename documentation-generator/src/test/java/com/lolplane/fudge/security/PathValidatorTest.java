package com.lolplane.fudge.security;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.lolplane.fudge.security.PathValidator.isPathSafe;
import static com.lolplane.fudge.security.PathValidator.isPathWithinBase;
import static org.assertj.core.api.Assertions.assertThat;

class PathValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "./../etc/passwd",
        "subdir/../../etc/passwd",
        "./././../etc/passwd",
        "./"
    })
    @DisplayName("should detect unsafe string paths with path traversal")
    void shouldDetectUnsafeStringPathsWithPathTraversal(String path) {
        assertThat(isPathSafe(path)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "normal/path",
        "documents",
        "images/photo.jpg",
        "data.txt",
        "folder/subfolder/file.pdf"
    })
    @DisplayName("should allow safe string paths")
    void shouldAllowSafeStringPaths(String path) {
        assertThat(isPathSafe(path)).isTrue();
    }

    @Test
    @DisplayName("should reject null string paths")
    void shouldRejectNullStringPaths() {
        assertThat(isPathSafe((String) null)).isFalse();
    }

    @Test
    @DisplayName("should allow empty string path")
    void shouldAllowEmptyStringPath() {
        assertThat(isPathSafe("")).isTrue();
    }

    @Test
    @DisplayName("should reject null Path object")
    void shouldRejectNullPathObject() {
        Path nullPath = null;
        assertThat(isPathSafe(nullPath)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "subdir/../../etc/passwd"
    })
    @DisplayName("should detect unsafe Path objects with path traversal")
    void shouldDetectUnsafePathObjectsWithPathTraversal(String pathStr) {
        var path = Paths.get(pathStr);
        var normalizedPath = path.normalize();
        // Only test paths where normalization changes the path
        if (!path.toString().equals(normalizedPath.toString())) {
            assertThat(isPathSafe(path)).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "normal/path",
        "documents",
        "images/photo.jpg",
        "data.txt",
        "folder/subfolder/file.pdf"
    })
    @DisplayName("should allow safe Path objects")
    void shouldAllowSafePathObjects(String pathStr) {
        var path = Paths.get(pathStr);
        assertThat(isPathSafe(path)).isTrue();
    }

    @Test
    @DisplayName("should reject null base path or target path")
    void shouldRejectNullBasePathOrTargetPath() {
        var basePath = Paths.get("base");
        var targetPath = Paths.get("base/target");
        Path nullPath = null;

        assertThat(isPathWithinBase(nullPath, targetPath)).isFalse();
        assertThat(isPathWithinBase(basePath, nullPath)).isFalse();
        assertThat(isPathWithinBase(nullPath, nullPath)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        base, base/target, true
        base, base, true
        base, base/subdir/file.txt, true
        base, other, false
        base, ../base, false
        base, base/../other, false
        base/dir, base, false
        /root/base, /root/base/target, true
        /root/base, /root/other, false
        /root/base, /root/base/../other, false
        """
    )
    @DisplayName("should correctly validate if path is within base directory")
    void shouldCorrectlyValidateIfPathIsWithinBaseDirectory(String baseDirStr, String targetPathStr, boolean expected) {
        var baseDir = Paths.get(baseDirStr);
        var targetPath = Paths.get(targetPathStr);

        assertThat(isPathWithinBase(baseDir, targetPath)).isEqualTo(expected);
    }
}
