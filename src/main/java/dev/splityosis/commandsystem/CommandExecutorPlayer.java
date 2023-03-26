package dev.splityosis.commandsystem;

import org.bukkit.entity.Player;

public interface CommandExecutorPlayer{
    public void executes(Player sender, String[] args);
}