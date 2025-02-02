package com.lolplane.fudge;

import java.util.List;

import com.lolplane.fudge.cli.ProgramConfiguration;

public record ProgramConfigurationAndErrors(ProgramConfiguration configuration, List<Exception> errors) {
}
