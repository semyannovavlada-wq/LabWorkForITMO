package org.example;

import org.example.cli.services.CommandService;
import org.example.cli.services.LoopService;

public class App {
    public static void main(String[] args) {
        System.out.println("Samples and Measurements");
        System.out.println("type help for all commands");
        System.out.println();

        CommandService commandService = new CommandService();
        LoopService loopService = new LoopService(commandService);
        loopService.loopOfCommands();

        System.out.println("Program finished.");
    }
}