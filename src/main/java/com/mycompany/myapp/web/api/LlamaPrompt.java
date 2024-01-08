package com.mycompany.myapp.web.api;

import java.util.List;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.AssistantMessage;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.SystemMessage;
import org.springframework.ai.prompt.messages.UserMessage;

public class LlamaPrompt extends Prompt {

    public LlamaPrompt(List<Message> messages) {
        super(messages);
    }

    @Override
    public String getContents() {
        var sb = new StringBuilder();
        var messages = getMessages();
        var systemMessage = messages
            .stream()
            .filter(m -> m instanceof SystemMessage)
            .map(m -> (SystemMessage) m)
            .reduce((first, second) -> second)
            .orElseThrow();
        var userMessages = messages.stream().filter(m -> m instanceof UserMessage).map(m -> (UserMessage) m).toList();
        var assistantMessages = messages.stream().filter(m -> m instanceof AssistantMessage).map(m -> (AssistantMessage) m).toList();

        for (int i = 0; i < userMessages.size(); i++) {
            var userMessage = userMessages.get(i);
            var assistantMessage = i < assistantMessages.size() ? assistantMessages.get(i) : null;

            sb.append("<s>");
            if (i == 0) {
                sb.append("<<SYS>>\n").append(systemMessage.getContent()).append("\n<</SYS>>");
            }
            sb.append("[INST]");
            sb.append(userMessage.getContent());
            sb.append("[/INST] ");
            sb.append(assistantMessage != null ? assistantMessage.getContent() : "");

            if (i < userMessages.size() - 1) {
                sb.append(" </s>");
            }
        }
        return sb.toString();
    }
}
