package net.etum.etumeconomy.Events;

import net.etum.etumeconomy.Main;
import net.etum.etumeconomy.Manager.BalanceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class ConnectionManager implements Listener {

    @EventHandler
    public void onJoinServer(PlayerJoinEvent event){

        if(!(Objects.equals(Main.getConfigManager().getInformationStorageFormat(), "YAML"))){
            Player player = event.getPlayer();

            BalanceManager balanceManager = new BalanceManager();

            Main.getEconomy().withdrawPlayer(player, Main.getEconomy().getBalance(player));
            Main.getEconomy().depositPlayer(player, balanceManager.getBalance(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onQuitServer(PlayerQuitEvent event){
        if(!(Objects.equals(Main.getConfigManager().getInformationStorageFormat(), "YAML"))){
            Player player = event.getPlayer();

            BalanceManager balanceManager = new BalanceManager();

            balanceManager.setBalance(player.getUniqueId(), Main.getEconomy().getBalance(player));
        }
    }




}
