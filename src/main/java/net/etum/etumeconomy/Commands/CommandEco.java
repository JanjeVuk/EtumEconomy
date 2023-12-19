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

    private Economy economy;

    public CommandEco(Economy economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            // Affichez un message d'aide si la commande n'a pas suffisamment d'arguments.
            // Par exemple: /eco give <joueur> <montant>
            sender.sendMessage(ChatColor.RED + "Utilisation: /eco <give/set/take/reset> <joueur> <montant>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("give") || subCommand.equals("set") || subCommand.equals("take") || subCommand.equals("reset")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /eco " + subCommand + " <joueur> <montant>");
                return true;
            }

            String playerName = args[1];
            Player targetPlayer = Bukkit.getPlayer(playerName);

            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Le joueur spécifié n'est pas en ligne.");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Le montant doit être un nombre valide.");
                return true;
            }

            switch (subCommand) {
                case "give":
                    economy.depositPlayer(targetPlayer, amount);
                    sender.sendMessage(ChatColor.GREEN + "Vous avez donné " + amount + " à " + targetPlayer.getName());
                    break;
                case "set":
                    economy.withdrawPlayer(targetPlayer, economy.getBalance(targetPlayer));
                    economy.depositPlayer(targetPlayer, amount);
                    sender.sendMessage(ChatColor.GREEN + "Vous avez défini le solde de " + targetPlayer.getName() + " à " + amount);
                    break;
                case "take":
                    economy.withdrawPlayer(targetPlayer, amount);
                    sender.sendMessage(ChatColor.GREEN + "Vous avez pris " + amount + " de " + targetPlayer.getName());
                    break;
                case "reset":
                    economy.withdrawPlayer(targetPlayer, economy.getBalance(targetPlayer));
                    sender.sendMessage(ChatColor.GREEN + "Vous avez réinitialisé le solde de " + targetPlayer.getName() + " à 0");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Commande inconnue. Utilisation: /eco <give/set/take/reset> <joueur> <montant>");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            // Si l'utilisateur n'a tapé qu'une partie de la commande, proposez les sous-commandes possibles
            List<String> subCommands = new ArrayList<>();
            subCommands.add("give");
            subCommands.add("set");
            subCommands.add("take");
            subCommands.add("reset");
            return subCommands;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // Si la sous-commande est 'give', proposez les noms des joueurs en ligne
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }

        // Pour d'autres cas, retournez une liste vide pour ne pas suggérer de complétions automatiques
        return new ArrayList<>();
    }
}
