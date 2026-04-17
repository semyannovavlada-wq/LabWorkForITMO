package org.example.cli.services;

import org.example.cli.handlers.*;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import java.util.HashMap;
import java.util.Map;

public class CommandService {
    private final Map<String, BaseHandler> commandList;
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;

    public CommandService() {
        this.sampleService = new SampleService("data");
        this.measurementService = new MeasurementService(sampleService, "data");
        this.protocolService = new ProtocolService(sampleService, measurementService, "data");

        this.commandList = new HashMap<>();
        this.commandList.put("exit", new ExitHandler());
        this.commandList.put("help", new HelpHandler());
        this.commandList.put("save", new SaveHandler());
        this.commandList.put("load", new LoadHandler());
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
    }

    public boolean readCommand(java.util.List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            System.out.println("Command not found");
            return true;
        }

        String command = commands.get(0);
        BaseHandler handler = commandList.get(command);

        if (handler != null) {
            java.util.List<String> args = commands.subList(1, commands.size());
            return handler.handle(args, sampleService, measurementService, protocolService, commandList.values());
        }

        System.out.println("Command not found");
        return true;
    }
}