package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;
import dev.splityosis.commandsystem.SYSCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Override
    @NonNull
    public List<String> tabComplete(CommandSender sender, SYSCommand command, String input) {
        List<String> names = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().toLowerCase().startsWith(input))
                names.add(onlinePlayer.getName());
        }
        Collections.sort(names);
        return names;
    }
}
