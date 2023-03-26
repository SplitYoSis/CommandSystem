package dev.splityosis.commandsystem;

import org.bukkit.command.CommandSender;
import java.util.List;

public abstract class SYSCondition {

    public abstract boolean isValid(CommandSender sender);
    public abstract List<String> getConditionNotMetMessage(CommandSender sender);
}