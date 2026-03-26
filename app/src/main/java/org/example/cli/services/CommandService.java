package org.example.cli.services;

import org.example.cli.handlers.BaseHandler;
import org.example.cli.handlers.ExitHandler;
import org.example.cli.handlers.HelpHandler;
import org.example.services.SampleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandService {
    private final Map<String, BaseHandler> commandList;
    private final org.example.services.SampleService sampleService;

    public CommandService() {
        this.commandList = new HashMap<>();
        this.commandList.put("exit", new ExitHandler());
        this.commandList.put("help", new HelpHandler());

        this.sampleService = new org.example.services.SampleService();
    }

    public boolean readCommand(List<String> commands) {
        // Проверяем, что список не пустой и получаем команду
        if (commands == null || commands.isEmpty()) {
            System.out.println("Command not found");
            return true;
        }

        String command = commands.get(0);
        BaseHandler handler = commandList.get(command);

        if (handler != null) {
            List<String> args = commands.subList(1, commands.size());
            return handler.handle(args, sampleService, commandList.values());
        }

        System.out.println("Command not found");
        return true;
    }
}