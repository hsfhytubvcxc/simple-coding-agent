package dev.agent.runtime.service;

import dev.agent.runtime.model.CommandResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationRuntimeService {
    private static final String IMAGE = "coding-agent-app:local";
    private static final int COMMAND_TIMEOUT_SECONDS = 120;

    private final DockerCli docker;

    public int open(UUID chatId) throws IOException, InterruptedException {
        String name = "coding-agent-app-" + chatId;
        CommandResult state = docker.execute("inspect", "--format", "{{.State.Running}}", name);
        if (state.exitCode() != 0) {
            docker.checked("run", "-d", "--name", name,
                    "--label", "coding-agent.app=true",
                    "--init",
                    "--network", "coding-agent-apps",
                    "--publish", "127.0.0.1::3000",
                    "--mount", "type=volume,source=coding-agent-workspace-" + chatId + ",target=/workspace",
                    IMAGE);
        } else if (!state.output().strip().equals("true")) {
            docker.checked("start", name);
        }

        // Wait for the server, even when the generated page has a compilation error.
        docker.checked("exec", name, "bash", "-lc", """
                for attempt in {1..30}; do
                  (echo > /dev/tcp/127.0.0.1/3000) 2>/dev/null && exit 0
                  sleep 1
                done
                exit 1
                """);
        String address = docker.checked("port", name, "3000/tcp").strip();
        return Integer.parseInt(address.substring(address.lastIndexOf(':') + 1));
    }

    public CommandResult execute(UUID chatId, String command) throws IOException, InterruptedException {
        return docker.execute("exec", "--workdir", "/workspace",
                "coding-agent-app-" + chatId, "timeout", "--signal=KILL",
                String.valueOf(COMMAND_TIMEOUT_SECONDS), "bash", "-lc", command);
    }

    public void delete(UUID chatId) throws IOException, InterruptedException {
        docker.checked("rm", "-f", "coding-agent-app-" + chatId);
        docker.checked("volume", "rm", "coding-agent-workspace-" + chatId);
    }
}
