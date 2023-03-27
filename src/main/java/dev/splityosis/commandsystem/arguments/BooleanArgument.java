package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;
import org.bukkit.ChatColor;

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
}