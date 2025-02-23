package com.lolplane.fudge.mojos;

import java.util.List;

import com.lolplane.fudge.FeatureDocumentationGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenerateDocsMojoTest {

    @Mock
    private FeatureDocumentationGenerator documentationGenerator;
    @InjectMocks
    private GenerateDocsMojo mojo;

    @SneakyThrows
    @Test
    @DisplayName("should call document generator with default options")
    void shouldCallDocumentGeneratorWithDefaultOptions() {
        mojo.setJgivenOutputDirectory("jgiven-output");
        mojo.setFudgeOutputDirectory("fudge-output");
        mojo.execute();
        verify(documentationGenerator).parseCommandLineAndGenerateDocumentation(any(), eq(List.of("-s", "jgiven-output", "-t", "fudge-output")));
    }

    @SneakyThrows
    @Test
    @DisplayName("should turn on verbose mode if configured")
    void shouldTurnOnVerboseModeIfConfigured() {
        mojo.setJgivenOutputDirectory("jgiven-output");
        mojo.setFudgeOutputDirectory("fudge-output");
        mojo.setVerboseMode(true);
        mojo.execute();
        verify(documentationGenerator).parseCommandLineAndGenerateDocumentation(any(), eq(List.of("-s", "jgiven-output", "-t", "fudge-output", "-v")));
    }

    @SneakyThrows
    @Test
    @DisplayName("should turn on dry mode if configured")
    void shouldTurnOnDryModeIfConfigured() {
        mojo.setJgivenOutputDirectory("jgiven-output");
        mojo.setFudgeOutputDirectory("fudge-output");
        mojo.setDryMode(true);
        mojo.execute();
        verify(documentationGenerator).parseCommandLineAndGenerateDocumentation(any(), eq(List.of("-s", "jgiven-output", "-t", "fudge-output", "-n")));
    }

}
