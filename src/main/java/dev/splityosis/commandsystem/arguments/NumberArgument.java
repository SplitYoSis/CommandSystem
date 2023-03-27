package dev.splityosis.commandsystem.arguments;

import dev.splityosis.commandsystem.SYSArgument;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class NumberArgument extends SYSArgument {

    @Override
    public boolean isValid(String input) {
        try{
            Double.parseDouble(input);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public List<String> getInvalidInputMessage(String input) {
        return Arrays.asList(ChatColor.RED + "Invalid input at '"+input+"', Please provide a number.");
    }
}