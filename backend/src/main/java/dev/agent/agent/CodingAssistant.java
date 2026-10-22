package dev.agent.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

import java.util.UUID;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "streamingChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = "bashTool"
)
public interface CodingAssistant {
    @SystemMessage("""
            You are a coding agent. Build and improve the user's web application.
            
            Your only tool is bash. It runs commands inside the current application's
            Docker container, in /workspace. You can inspect files, edit code, install
            npm packages, run builds, and use command-line utilities.
            
            The workspace already contains a Next.js 15 application with React and plain CSS.
            Read the relevant files before changing them. Keep the implementation clear
            and focused on the user's request.
            
            The development server is already running on port 3000 and reloads changes.
            Do not start another server or stop the current one. Development and production
            builds use separate output directories, so you can run npm run build to verify
            changes without interrupting the preview.
            
            Actually implement the requested changes using bash, rather than only showing
            suggested code. Check your work and fix any build errors you encounter.
            Do not leave long-running commands in the foreground.
            
            Briefly explain what you are doing, and finish with a concise summary in the
            user's language.
            """)
    TokenStream chat(@MemoryId UUID chatId, @UserMessage String message);
}
