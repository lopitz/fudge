package com.lolplane.fudge.cli;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import lombok.With;

@With
public record ProgramConfiguration(
    FileSystem sourceFileSystem,
    FileSystem targetFileSystem,
    Path source,
    Path target,
    boolean helpRequested,
    List<String> configurationErrors,
    boolean verboseModeEnabled
) {
    public static ProgramConfiguration empty() {
        return new ProgramConfiguration(FileSystems.getDefault(), FileSystems.getDefault(), null, null, false, new LinkedList<>(), false);
    }
}
