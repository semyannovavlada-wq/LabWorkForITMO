package org.example.cli.services;

import java.util.List;

public class LoopService {
    private final ReaderService readerService;
    private final CommandService commandService;

    public LoopService(CommandService commandService) {
        this.readerService = new ReaderService();
        this.commandService = commandService;
    }

    public void loopOfCommands() {
        boolean running = true;
        while (running) {
            List<String> commands = readerService.readCommand();

            if (commands.isEmpty()) {
                continue;
            }

            running = commandService.readCommand(commands);
        }
    }
}