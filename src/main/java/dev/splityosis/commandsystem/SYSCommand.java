package dev.splityosis.commandsystem;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SYSCommand {

    private String name;
    private List<String> aliases = new ArrayList<>();
    private String permission;
    private String usage = "";
    private SYSArgument[] arguments;
    private SYSCondition[] conditions;
    private CommandExecutorPlayer commandExecutorPlayer;
    private CommandExecutorSender commandExecutorSender;
    private BukkitCommand bukkitCommand;

    public SYSCommand(String... names) {
        this.name = names[0].toLowerCase();
        for (int i = 1; i < names.length; i++)
            aliases.add(names[i].toLowerCase());
    }

    public String getName() {
        return name;
    }

    public CommandExecutorSender getCommandExecutorSender() {
        return commandExecutorSender;
    }

    public CommandExecutorPlayer getCommandExecutorPlayer() {
        return commandExecutorPlayer;
    }

    public String getUsage() {
        return usage;
    }

    public SYSCommand setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public List<String> getInvalidUsageMessage(){
        return Util.replaceList(SYSConfig.getUseCorrectionMessage(), "%usage%", getUsage());
    }


    public List<String> getAliases() {
        return aliases;
    }

    public SYSCommand setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public String getPermission() {
        return permission;
    }

    public SYSArgument[] getArguments() {
        return arguments;
    }

    public SYSCondition[] getConditions() {
        return conditions;
    }

    public SYSCommand setConditions(SYSCondition... conditions){
        this.conditions = conditions;
        return this;
    }

    public SYSCommand setArguments(SYSArgument... arguments){
        this.arguments = arguments;
        return this;
    }

    public SYSCommand executes(CommandExecutorSender executor){
        commandExecutorSender = executor;
        return this;
    }

    public SYSCommand executesPlayer(CommandExecutorPlayer executor){
        commandExecutorPlayer = executor;
        return this;
    }

    public BukkitCommand getBukkitCommand() {
        return bukkitCommand;
    }

    public void registerCommand(JavaPlugin plugin){
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            bukkitCommand = new BukkitCommand(name, "", "", aliases) {

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                    return tabCompleter(sender, args);
                }

                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return runCommand(sender, args);
                }
            };
            if (permission != null)
                bukkitCommand.setPermission(permission);
            commandMap.register(name, bukkitCommand);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterFromCommandMap() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            Map<String, Command> knownCommands = Util.getKnownCommands(commandMap);

            if (bukkitCommand != null)
                bukkitCommand.unregister(commandMap);

            knownCommands.remove(name);
            for (String alias : aliases)
                knownCommands.remove(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> tabCompleter(CommandSender sender, String[] args){
        if (permission != null && !sender.hasPermission(permission)) return new ArrayList<>();
        if (arguments == null || args.length > arguments.length) return new ArrayList<>();
        SYSArgument arg = arguments[args.length-1];
        return arg.tabComplete(sender, this, args[args.length-1]);
    }

    public void registerToBranch(SYSCommandBranch branch){
        branch.addCommand(this);
    }

    public boolean runCommand(CommandSender sender, String[] args){
        return runCommand(this, sender, args);
    }

    public static boolean runCommand(SYSCommand command, CommandSender sender, String[] args){
        if (command.getCommandExecutorSender() == null && !(sender instanceof Player)){
            Util.sendMessage(sender, "&cOnly players can use this command");
            return false;
        }

        if (command.conditions != null){
            for (SYSCondition condition : command.getConditions()) {
                if (!condition.isValid(sender)){
                    Util.sendMessage(sender, condition.getConditionNotMetMessage(sender));
                    return false;
                }
            }
        }

        if (command.getArguments() != null) {
            if (args.length < command.getArguments().length) {
                Util.sendMessage(sender, command.getInvalidUsageMessage());
                return false;
            }

            for (int i = 0; i < command.getArguments().length; i++) {
                if (!command.getArguments()[i].isValid(args[i])) {
                    Util.sendMessage(sender, command.getArguments()[i].getInvalidInputMessage(args[i]));
                    return false;
                }
            }
        }

        if (sender instanceof Player && command.getCommandExecutorPlayer() != null) {
            command.getCommandExecutorPlayer().executes((Player) sender, args);
            return true;
        }
        if (command.getCommandExecutorSender() != null) {
            command.getCommandExecutorSender().executes(sender, args);
            return true;
        }
        else Util.sendMessage(sender, "&cOnly players can use this command");

        return false;
    }
}