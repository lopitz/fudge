package com.lolplane.fudge.generation;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FolderCreator {

    private final ConsoleWriter consoleWriter;
    private final FileSystem fileSystem;
    private final PathNameCreator pathNameCreator = new PathNameCreator();

    public Path createFolder(String targetedName, Path rootTargetPath) {
        var folderName = pathNameCreator.createPathName(targetedName);
        return Optional
            .ofNullable(createFolder(rootTargetPath, folderName))
            .orElseGet(() -> {
                var hashBasedName = Integer.toHexString(folderName.hashCode());
                return createFolder(rootTargetPath, hashBasedName);
            });
    }

    private Path createFolder(Path rootTargetPath, String folderName) {
        try {
            var targetPath = fileSystem.getPath(rootTargetPath.toString(), folderName);
            if (Files.exists(targetPath)) {
                return null;
            }
            var result = Files.createDirectories(targetPath);
            consoleWriter.debug("Created folder {}", folderName);
            return result;
        } catch (IOException e) {
            consoleWriter.warn("Error creating folder {}", folderName, e);
        }
        return null;
    }


}
