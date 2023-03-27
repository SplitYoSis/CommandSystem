package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class WordArgument extends SYSArgument {
    @Override
    public boolean isValid(String input) {
        for (char c : input.toCharArray()){
            if (c <= 'z' && c >= 'a') continue;
            if (c <= 'Z' && c >= 'A') continue;
            return false;
        }
        return true;
    }

    @Override
    public List<String> getInvalidInputMessage(String input) {
        return Arrays.asList(ChatColor.RED + "Invalid input at '"+input+"', Please provide a word that only contains english letters.");
    }
}