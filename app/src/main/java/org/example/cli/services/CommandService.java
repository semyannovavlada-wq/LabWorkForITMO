package org.example.cli.services;

import org.example.cli.handlers.*;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.StorageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandService {
    private final Map<String, BaseHandler> commandList;
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;

    public CommandService(SampleService sampleService,
                          MeasurementService measurementService,
                          ProtocolService protocolService) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;
        StorageService storageService = new StorageService(sampleService, measurementService, protocolService);

        this.commandList = new HashMap<>();
        this.commandList.put("exit", new ExitHandler());
        this.commandList.put("help", new HelpHandler());
        this.commandList.put("MeasAdd", new MeasAddHandler());
        this.commandList.put("MeasList", new MeasListHandler());
        this.commandList.put("MeasStat", new MeasStatsHandler());
        this.commandList.put("ProtApply", new ProtApplyHandler());
        this.commandList.put("ProtCreate", new ProtCreateHandler());
        this.commandList.put("SampleAdd", new SampleAddHandler());
        this.commandList.put("SampleArchive", new SampleArchiveHandler());
        this.commandList.put("SampleList", new SampleListHandler());
        this.commandList.put("SampleShow", new SampleShowHandler());
        this.commandList.put("SampleUpdate", new SampleUpdateHandler());
        this.commandList.put("save", new SaveHandler(storageService));
        this.commandList.put("load", new LoadHandler(storageService));
    }

    public boolean readCommand(List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            System.out.println("Command not found");
            return true;
        }

        String command = commands.get(0);
        BaseHandler handler = commandList.get(command);

        if (handler != null) {
            List<String> args = commands.subList(1, commands.size());
            return handler.handle(args, sampleService, measurementService, protocolService, commandList.values());
        }

        System.out.println("Command not found");
        return true;
    }
}