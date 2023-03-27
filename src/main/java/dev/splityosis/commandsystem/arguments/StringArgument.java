package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;

import java.util.List;

public class StringArgument extends SYSArgument {
    @Override
    public boolean isValid(String input) {
        return true;
    }

    @Override
    public List<String> getInvalidInputMessage(String input) {
        return null;
    }
}