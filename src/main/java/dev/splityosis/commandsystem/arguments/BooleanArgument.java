package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;
import dev.splityosis.commandsystem.SYSCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BooleanArgument extends SYSArgument {

    @Override
    public boolean isValid(String input) {
        return input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false");
    }

    @Override
    public List<String> getInvalidInputMessage(String input) {
        return Arrays.asList(ChatColor.RED + "Invalid input at '"+input+"', Please provide a true/false value.");
    }

    @Override
    public @NonNull List<String> tabComplete(CommandSender sender, SYSCommand command, String input) {
        List<String> bools = new ArrayList<>();
        if ("false".startsWith(input.toLowerCase()))
            bools.add("False");
        if ("true".startsWith(input.toLowerCase()))
            bools.add("True");
        return bools;
    }
}