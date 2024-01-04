package net.etum.etumeconomy.Commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandEco implements TabExecutor {

    private final Economy economy;

    public CommandEco(Economy economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /eco <give/set/take/reset/balance/top> <player> [amount]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (List.of("give", "set", "take", "reset", "balance", "top").contains(subCommand)) {
            switch (subCommand) {
                case "give":
                case "set":
                case "take":
                case "reset":
                    handleBalanceModification(sender, args);
                    break;
                case "balance":
                    handleBalanceCheck(sender, args);
                    break;
                case "top":
                    handleTopPlayers(sender);
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown command. Usage: /eco <give/set/take/reset/balance/top> <player> [amount]");
        }

        return true;
    }

    private void handleBalanceModification(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /eco " + args[0] + " <player> <amount>");
            return;
        }

        String playerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (validatePlayer(sender, targetPlayer)) {
            double amount = parseAmount(sender, args[2]);
            if (amount >= 0) {
                performBalanceModification(sender, args[0], targetPlayer, amount);
            }
        }
    }

    private void handleBalanceCheck(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /eco balance <player>");
            return;
        }

        String playerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (validatePlayer(sender, targetPlayer)) {
            double balance = economy.getBalance(targetPlayer);
            sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + "'s balance: " + balance);
        }
    }

    private void handleTopPlayers(CommandSender sender) {
        // Implement this method to display top players based on your specific logic
    }

    private boolean validatePlayer(CommandSender sender, Player player) {
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "The specified player is not online.");
            return false;
        }
        return true;
    }

    private double parseAmount(CommandSender sender, String amountString) {
        try {
            return Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The amount must be a valid number.");
            return -1;
        }
    }

    private void performBalanceModification(CommandSender sender, @NotNull String operation, Player targetPlayer, double amount) {
        switch (operation) {
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
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(List.of("give", "set", "take", "reset", "balance", "top"));
        } else if (args.length == 2) {
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return completions;
    }
}