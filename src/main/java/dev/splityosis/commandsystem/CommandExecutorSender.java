package dev.splityosis.commandsystem;

import org.bukkit.command.CommandSender;

public interface CommandExecutorSender{
    public void executes(CommandSender sender, String[] args);
}