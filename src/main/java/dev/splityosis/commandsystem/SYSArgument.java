package dev.splityosis.commandsystem;

import java.util.List;

public abstract class SYSArgument {

    public abstract boolean isValid(String input);

    public abstract List<String> getInvalidInputMessage(String input);
}