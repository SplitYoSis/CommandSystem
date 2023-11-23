package dev.splityosis.commandsystem;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

public class SYSCommandBranch {

    private String name;
    private List<String> aliases = new ArrayList<>();
    private String permission = "";
    private List<String> unknownCommandMessage = Arrays.asList("&cUnknown sub-command.");
    private SYSCondition[] conditions;
    private Map<String, SYSCommandBranch> innerBranches = new HashMap<>();
    private Map<String, SYSCommand> innerCommands = new HashMap<>();
    private CommandExecutorPlayer commandExecutorPlayer;
    private CommandExecutorSender commandExecutorSender;
    private BukkitCommand bukkitCommand;

    public SYSCommandBranch(String... names) {
        this.name = names[0].toLowerCase();
        for (int i = 1; i < names.length; i++)
            aliases.add(names[i].toLowerCase());
    }

    public SYSCommandBranch addBranch(SYSCommandBranch commandBranch){
        innerBranches.put(commandBranch.name, commandBranch);
        for (String alias : commandBranch.aliases)
            innerBranches.put(alias, commandBranch);
        return this;
    }

    public SYSCommandBranch removeBranch(SYSCommandBranch commandBranch){
        innerBranches.remove(commandBranch.name);
        for (String alias : commandBranch.aliases)
            innerBranches.remove(alias);
        return this;
    }

    public SYSCommandBranch addCommand(SYSCommand command){
        innerCommands.put(command.getName(), command);
        for (String alias : command.getAliases())
            innerCommands.put(alias, command);
        return this;
    }

    public SYSCommandBranch removeCommand(SYSCommand command){
        innerCommands.remove(command.getName());
        for (String alias : command.getAliases())
            innerCommands.remove(alias);
        return this;
    }

    public SYSCommandBranch setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public SYSCommandBranch setUnknownCommandMessage(List<String> unknownCommandMessage) {
        this.unknownCommandMessage = unknownCommandMessage;
        return this;
    }

    public SYSCommandBranch setConditions(SYSCondition[] conditions) {
        this.conditions = conditions;
        return this;
    }

    public SYSCondition[] getConditions() {
        return conditions;
    }

    public String getPermission() {
        return permission;
    }

    public Map<String, SYSCommand> getInnerCommands() {
        return innerCommands;
    }

    public Map<String, SYSCommandBranch> getInnerBranches() {
        return innerBranches;
    }

    public boolean runBranch(CommandSender sender, String[] args){
        return runBranch(this, sender, args);
    }

    public SYSCommandBranch baseExecutes(CommandExecutorSender executor){
        commandExecutorSender = executor;
        return this;
    }

    public SYSCommandBranch baseExecutesPlayer(CommandExecutorPlayer executor){
        commandExecutorPlayer = executor;
        return this;
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

    public BukkitCommand getBukkitCommand() {
        return bukkitCommand;
    }

    public void registerCommandBranch(JavaPlugin plugin){
        SYSCommandBranch instance = this;
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            bukkitCommand = new BukkitCommand(name, "", "", aliases) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return runBranch(sender, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                    SYSCommandBranch branch = instance;
                    if (args.length > 1) {
                        for (int i = 0; i < args.length-1; i++) {
                            SYSCommandBranch currentBranch = branch.getInnerBranches().get(args[i].toLowerCase());
                            if (currentBranch != null) {
                                branch = currentBranch;
                                continue;
                            }

                            SYSCommand currentCommand = branch.innerCommands.get(args[i].toLowerCase());
                            if (currentCommand != null){
                                return currentCommand.tabCompleter(sender, Arrays.copyOfRange(args, i+1, args.length));
                            }
                        }
                    }

                    String input = args[args.length-1];
                    List<String> subCommands = new ArrayList<>();
                    for (SYSCommandBranch value : branch.getInnerBranches().values()) {
                        if (subCommands.contains(value.getName())) continue;
                        if (value.getName().toLowerCase().startsWith(input.toLowerCase()))
                            subCommands.add(value.getName());
                    }

                    for (SYSCommand value : branch.getInnerCommands().values()) {
                        if (subCommands.contains(value.getName())) continue;
                        if (value.getName().toLowerCase().startsWith(input.toLowerCase()))
                            subCommands.add(value.getName());
                    }
                    Collections.sort(subCommands);
                    return subCommands;
                }
            };
            if (permission != null)
                bukkitCommand.setPermission(permission);
            commandMap.register(name, bukkitCommand);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void registerToCommandBranch(SYSCommandBranch branch){
        branch.addBranch(this);
    }

    public boolean runBranch(SYSCommandBranch branch, CommandSender sender, String[] args){
        if (branch.conditions != null){
            for (SYSCondition condition : branch.getConditions()) {
                if (!condition.isValid(sender)){
                    Util.sendMessage(sender, condition.getConditionNotMetMessage(sender));
                    return false;
                }
            }
        }

        if (args.length == 0){
            if (sender instanceof Player && branch.commandExecutorPlayer != null) {
                branch.commandExecutorPlayer.executes((Player) sender, args);
                return true;
            }
            if (branch.commandExecutorSender != null) {
                branch.commandExecutorSender.executes(sender, args);
                return true;
            }
            else {
                if (branch.commandExecutorPlayer != null)
                    Util.sendMessage(sender, "&cOnly players can use this command");
                else Util.sendMessage(sender, unknownCommandMessage);
            }
            return false;
        }

        if (branch.innerCommands.containsKey(args[0].toLowerCase())){
            SYSCommand cmd = branch.innerCommands.get(args[0].toLowerCase());
            if (cmd.getPermission() != null && !sender.hasPermission(cmd.getPermission())){
                Util.sendMessage(sender, SYSConfig.getNoPermissionMessage());
                return false;
            }
            cmd.runCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        if (branch.innerBranches.containsKey(args[0].toLowerCase())){
            SYSCommandBranch cBranch = branch.innerBranches.get(args[0].toLowerCase());
            if (cBranch.getPermission() != null && !sender.hasPermission(cBranch.getPermission())){
                Util.sendMessage(sender, SYSConfig.getNoPermissionMessage());
                return false;
            }
            cBranch.runBranch(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        if (branch.unknownCommandMessage != null)
            Util.sendMessage(sender, branch.unknownCommandMessage);
        return false;
    }

    public String getName() {
        return name;
    }
}