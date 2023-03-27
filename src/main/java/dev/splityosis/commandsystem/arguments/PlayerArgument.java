package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class PlayerArgument extends SYSArgument {
    @Override
    public boolean isValid(String input) {
        return Bukkit.getPlayer(input) != null;
    }

    @Override
    public List<String> getInvalidInputMessage(String input) {
        return Arrays.asList(ChatColor.RED + "Player '"+input+"' isn't online.");
    }
}
