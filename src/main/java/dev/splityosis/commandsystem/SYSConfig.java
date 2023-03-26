package dev.splityosis.commandsystem;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public final class SYSConfig {

    private static List<String> noPermissionMessage = Arrays.asList(ChatColor.RED+ "You don't have permission to do this.");
    private static List<String> useCorrectionMessage = Arrays.asList(ChatColor.RED+ "Usage: %usage%");

    public static void setNoPermissionMessage(List<String> noPermissionMessage) {
        SYSConfig.noPermissionMessage = noPermissionMessage;
    }

    public static List<String> getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public static void setUseCorrectionMessage(List<String> useCorrectionMessage) {
        SYSConfig.useCorrectionMessage = useCorrectionMessage;
    }

    public static List<String> getUseCorrectionMessage() {
        return useCorrectionMessage;
    }
}