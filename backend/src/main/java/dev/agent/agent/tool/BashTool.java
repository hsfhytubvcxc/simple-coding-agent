package dev.agent.agent.tool;

import dev.agent.runtime.model.CommandResult;
import dev.agent.runtime.service.ApplicationRuntimeService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BashTool {
    private final ApplicationRuntimeService runtime;

    @Tool("Run a bash command in the current application's /workspace directory")
    public CommandResult bash(@ToolMemoryId UUID chatId, @P("Bash command to execute") String command)
            throws IOException, InterruptedException {
        return runtime.execute(chatId, command);
    }
}
