package edu.java.bot.commands;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandManager {
    private final Map<String, Command> commandMap;
    private static Map<String, String> commandDescriptionMap;
    @Getter
    private final Command unknownCommand;

    @Autowired
    public CommandManager(Map<String, Command> commandMap, Command unknownCommand) {
        this.commandMap = commandMap;
        commandMap.remove(unknownCommand.command());
        this.unknownCommand = unknownCommand;
        commandDescriptionMap = new HashMap<>();
        commandMap.forEach((key, value) -> commandDescriptionMap.put(value.command(), value.description()));
    }

    public Command retrieveCommand(String commandName) {
        return commandMap.getOrDefault(commandName, unknownCommand);
    }

    public boolean isUnknownCommand(Command command) {
        return unknownCommand.getClass() == command.getClass();
    }

    public static Map<String, String> getCommandDescription() {
        return commandDescriptionMap;
    }

}
