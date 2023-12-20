package net.etum.etumeconomy.Listeners;

import net.etum.etumeconomy.Commands.CommandEco;
import net.etum.etumeconomy.Main;

import java.util.Objects;

public class Commands {


    public Commands(Main main) {

        Objects.requireNonNull(main.getCommand("eco")).setExecutor(new CommandEco(Main.getEconomy()));


    }

}
