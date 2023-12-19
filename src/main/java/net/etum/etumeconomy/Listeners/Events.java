package net.etum.etumeconomy.Listeners;

import net.etum.etumeconomy.Main;
import org.bukkit.plugin.PluginManager;

public class Events {

    PluginManager pm;

    public Events(Main main) {

        pm = main.getServer().getPluginManager();

    }
}
