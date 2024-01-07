package net.etum.etumeconomy.Commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandEco implements TabExecutor {

    private final Economy economy;

    public CommandEco(Economy economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            // Display a help message if the command has insufficient arguments.
            // Example: /eco give <player> <amount>
            sender.sendMessage(ChatColor.RED + "Usage: /eco <give/set/take/reset> <player> <amount>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (Arrays.asList("give", "set", "take", "reset").contains(subCommand)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /eco " + subCommand + " <player> <amount>");
                return true;
            }

            String playerName = args[1];
            Player targetPlayer = Bukkit.getPlayer(playerName);

            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage(ChatColor.RED + "The specified player is not online.");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "The amount must be a valid number.");
                return true;
            }

            switch (subCommand) {
                case "give":
                    economy.depositPlayer(targetPlayer, amount);
                    sender.sendMessage(ChatColor.GREEN + "You have given " + amount + " to " + targetPlayer.getName());
                    break;
                case "set":
                    economy.withdrawPlayer(targetPlayer, economy.getBalance(targetPlayer));
                    economy.depositPlayer(targetPlayer, amount);
                    sender.sendMessage(ChatColor.GREEN + "You have set the balance of " + targetPlayer.getName() + " to " + amount);
                    break;
                case "take":
                    economy.withdrawPlayer(targetPlayer, amount);
                    sender.sendMessage(ChatColor.GREEN + "You have taken " + amount + " from " + targetPlayer.getName());
                    break;
                case "reset":
                    economy.withdrawPlayer(targetPlayer, economy.getBalance(targetPlayer));
                    sender.sendMessage(ChatColor.GREEN + "You have reset the balance of " + targetPlayer.getName() + " to 0");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown command. Usage: /eco <give/set/take/reset> <player> <amount>");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // If the user has only typed part of the command, suggest possible sub-commands
            completions.addAll(Arrays.asList("give", "set", "take", "reset"));
        } else if (args.length == 2) {
            // Suggest names of online players
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return completions;
    }
}
