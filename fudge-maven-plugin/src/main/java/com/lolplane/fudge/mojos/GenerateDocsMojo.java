package com.lolplane.fudge.mojos;

import java.util.ArrayList;
import java.util.List;

import com.lolplane.fudge.FeatureDocumentationGenerator;
import com.lolplane.fudge.utils.LogConsoleWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
    name = "generate-docs",
    defaultPhase = LifecyclePhase.SITE,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public class GenerateDocsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/site/fudge")
    private String fudgeOutputDirectory;
    @Parameter(defaultValue = "false")
    private boolean dryMode;
    @Parameter(defaultValue = "false")
    private boolean verboseMode;
    @Parameter(defaultValue = "${project.build.directory}/jgiven-reports/json")
    private String jgivenOutputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var consoleWriter = new LogConsoleWriter(getLog());
        try {
            var parameters = new ArrayList<>(List.of("-s", jgivenOutputDirectory.trim(), "-t", fudgeOutputDirectory.trim()));
            if (verboseMode) {
                parameters.add("-v");
            }
            if (dryMode) {
                parameters.add("-n");
            }
            getLog().debug("Running Fudge with parameters: " + String.join(" ", parameters));
            FeatureDocumentationGenerator.parseCommandLineAndGenerateDocumentation(consoleWriter, parameters);
        } catch (Exception e) {
            consoleWriter.error("Something went wrong.  Reason: {}", e.getMessage(), e);
        }
    }
}
