package com.lolplane.fudge.generation;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class FolderCreator {

    private static final String SPECIAL_FILESYSTEM_CHARACTERS = "[\\\\/?`'\"]";

    private final ConsoleWriter consoleWriter;
    private final FileSystem fileSystem;

    public Path createFolder(String targetedName, Path rootTargetPath) {
        var folderName = StringUtils.abbreviateMiddle(targetedName.replaceAll(SPECIAL_FILESYSTEM_CHARACTERS, "_"), "...", 250);
        return Optional
            .ofNullable(createFolder(rootTargetPath, folderName))
            .orElseGet(() -> {
                var uuidBasedName = UUID.randomUUID().toString();
                return createFolder(rootTargetPath, uuidBasedName);
            });
    }

    private Path createFolder(Path rootTargetPath, String folderName) {
        try {
            var targetPath = fileSystem.getPath(rootTargetPath.toString(), folderName);
            if (Files.exists(targetPath)) {
                return null;
            }
            return Files.createDirectories(targetPath);
        } catch (IOException e) {
            consoleWriter.warn("Error creating folder {}", folderName, e);
        }
        return null;
    }


}
