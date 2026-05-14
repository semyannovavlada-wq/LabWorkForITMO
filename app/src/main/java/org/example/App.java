package org.example;

import org.example.cli.services.CommandService;
import org.example.cli.services.LoopService;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.StorageService;

public class App {

    public static void main(String[] args) {
        System.out.println("type help for help");

        SampleService sampleService = new SampleService();
        MeasurementService measurementService = new MeasurementService(sampleService);
        ProtocolService protocolService = new ProtocolService(sampleService, measurementService);

        StorageService storageService = new StorageService(sampleService, measurementService, protocolService);

        storageService.loadAll();

        Runtime.getRuntime().addShutdownHook(new Thread(storageService::saveAll));

        CommandService commandService = new CommandService(sampleService, measurementService, protocolService);

        LoopService loopService = new LoopService(commandService);
        loopService.loopOfCommands();
    }
}