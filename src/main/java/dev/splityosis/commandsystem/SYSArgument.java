package dev.splityosis.commandsystem;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SYSArgument {

    public abstract boolean isValid(String input);

    public abstract List<String> getInvalidInputMessage(String input);

    @NonNull
    public List<String> tabComplete(CommandSender sender, SYSCommand command, String input){
        return new ArrayList<>();
    }
}