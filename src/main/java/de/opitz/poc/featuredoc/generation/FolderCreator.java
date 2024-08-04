package de.opitz.poc.featuredoc.generation;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class FolderCreator {

    private static final String SPECIAL_FILESYSTEM_CHARACTERS = "[\\\\/?`'\"]";

    private final FileSystem fileSystem;

    public FolderCreator() {
        this.fileSystem = FileSystems.getDefault();
    }

    public FolderCreator(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

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
            return Files.createDirectories(fileSystem.getPath(rootTargetPath.toString(), folderName));
        } catch (IOException e) {
            log.warn("Error creating folder {}", folderName, e);
        }
        return null;
    }


}
