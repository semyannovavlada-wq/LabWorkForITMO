package org.example.cli.services;

public class LoopService {
    private final ReaderService readerService;
    private final CommandService commandService;

    public LoopService() {
        this.readerService = new ReaderService();
        this.commandService = new CommandService();
    }

    public void loopOfCommands() {
        while (commandService.readCommand(readerService.readCommand())) {
        }
    }
}