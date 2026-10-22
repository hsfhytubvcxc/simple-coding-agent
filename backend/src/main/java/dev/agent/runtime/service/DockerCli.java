package dev.agent.runtime.service;

import dev.agent.runtime.model.CommandResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DockerCli {
    private static final int OUTPUT_LIMIT = 16000;

    public CommandResult execute(String... arguments) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>(List.of("docker"));
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        StringBuilder output = new StringBuilder();
        char[] buffer = new char[4096];
        try (var reader = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)) {
            int length;
            while ((length = reader.read(buffer)) != -1) {
                int remaining = OUTPUT_LIMIT - output.length();
                if (remaining > 0) {
                    output.append(buffer, 0, Math.min(length, remaining));
                }
            }
        }
        int exitCode = process.waitFor();
        if (output.length() == OUTPUT_LIMIT) {
            output.append("\n[Output truncated]");
        }
        return new CommandResult(exitCode, output.toString());
    }

    public String checked(String... arguments) throws IOException, InterruptedException {
        CommandResult result = execute(arguments);
        if (result.exitCode() != 0) {
            throw new IllegalStateException(result.output());
        }
        return result.output();
    }
}
