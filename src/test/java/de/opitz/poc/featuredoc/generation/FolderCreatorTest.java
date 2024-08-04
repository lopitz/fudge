package de.opitz.poc.featuredoc.generation;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.regex.Pattern;

import com.google.common.jimfs.Jimfs;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class FolderCreatorTest {

    private FolderCreator folderCreator;
    private FileSystem fileSystem;

    @BeforeEach
    void setupTest() {
        fileSystem = Jimfs.newFileSystem();
        folderCreator = new FolderCreator(fileSystem);
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

    @ParameterizedTest
    @CsvSource({
        "bla/blubb, bla_blubb",
        "bla\\blubb, bla_blubb",
        "bla'blubb, bla_blubb",
        "bla\"blubb, bla_blubb",
        "bla?blubb, bla_blubb",
    })
    @DisplayName("should replace special characters")
    void shouldReplaceSpecialCharacters(String folderName, String expected) {
        var actual = folderCreator.createFolder(folderName, fileSystem.getPath("/"));
        assertThat(actual).isEmptyDirectory().hasToString("/" + expected);
    }

    @Test
    @DisplayName("should limit folder name to 250 characters")
    void shouldLimitFolderNameTo250Characters() {
        var folderName = RandomStringUtils.randomAlphabetic(300);
        var actual = folderCreator.createFolder(folderName, fileSystem.getPath("/"));
        assertThat(actual.toString()).hasSize(251);
    }

    @SneakyThrows
    @Test
    @DisplayName("should create a uuid-based folder name in case folder could not be created")
    void shouldCreateAUuidBasedFolderNameInCaseFolderCouldNotBeCreated() {
        var folderName = "temp";
        Files.writeString(fileSystem.getPath("/", "temp"), "test");
        var actual = folderCreator.createFolder(folderName, fileSystem.getPath("/"));
        assertThat(actual.toString()).isNotEqualTo("/temp").matches(Pattern.compile("/\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}"));
    }

}
