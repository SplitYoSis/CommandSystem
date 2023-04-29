package dev.splityosis.commandsystem;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public interface SYSTabCompleter {

    @NonNull List<String> onTabComplete(CommandSender sender, SYSCommand command, String input);

}
