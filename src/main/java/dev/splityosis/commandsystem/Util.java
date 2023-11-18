package dev.splityosis.commandsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Util {

    public static void sendMessage(CommandSender to, String message){
        to.sendMessage(colorize(message));
    }

    public static void sendMessage(CommandSender to, List<String> message){
        message.forEach(s -> {
            sendMessage(to, s);
        });
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    public static String colorize(String str) {
        Matcher matcher = HEX_PATTERN.matcher(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', str));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static List<String> colorize(List<String> lst){
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }

    public static List<String> replaceList(List<String> lst, String from, String to){
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(s.replace(from, to));
        });
        return newList;
    }

    public static Map<String, Command> getKnownCommands(CommandMap commandMap) {
        try {
            // Dynamically get the class of the commandMap instance
            Class<?> clazz = commandMap.getClass();

            // Attempt to get the knownCommands field from this class
            Field knownCommandsField = null;
            while (clazz != null) {
                try {
                    knownCommandsField = clazz.getDeclaredField("knownCommands");
                    break;
                } catch (NoSuchFieldException e) {
                    // Move to the superclass if the field is not found in the current class
                    clazz = clazz.getSuperclass();
                }
            }

            if (knownCommandsField == null) {
                throw new NoSuchFieldException("Field 'knownCommands' not found in the commandMap class hierarchy.");
            }

            knownCommandsField.setAccessible(true);
            return (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}