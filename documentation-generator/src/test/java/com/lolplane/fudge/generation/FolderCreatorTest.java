package com.lolplane.fudge.generation;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.regex.Pattern;

import com.google.common.jimfs.Jimfs;
import com.lolplane.fudge.PrintWriterConsoleWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FolderCreatorTest {

    private FolderCreator folderCreator;
    private FileSystem fileSystem;

    @BeforeEach
    void setupTest() {
        fileSystem = Jimfs.newFileSystem();
        folderCreator = new FolderCreator(new PrintWriterConsoleWriter(), fileSystem);
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    @DisplayName("should create folder")
    void shouldCreateFolder() {
        var folderName = "temp";
        var actual = folderCreator.createFolder(folderName, fileSystem.getPath("/"));
        assertThat(actual).isEmptyDirectory().hasToString("/temp");
    }

    @SneakyThrows
    @Test
    @DisplayName("should create a hash-based folder name in case folder could not be created")
    void shouldCreateAHashBasedFolderNameInCaseFolderCouldNotBeCreated() {
        var folderName = "temp";
        Files.writeString(fileSystem.getPath("/", "temp"), "test");
        var actual = folderCreator.createFolder(folderName, fileSystem.getPath("/"));
        assertThat(actual.toString()).isNotEqualTo("/temp").matches(Pattern.compile("/([0-9a-f]{2}){1,4}"));
    }

}
