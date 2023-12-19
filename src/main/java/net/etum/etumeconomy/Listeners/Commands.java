package net.etum.etumeconomy.Listeners;

import net.etum.etumeconomy.Commands.CommandEco;
import net.etum.etumeconomy.Main;

public class Commands {


    public Commands(Main main) {

        main.getCommand("eco").setExecutor(new CommandEco(Main.getEconomy()));


    }

}
