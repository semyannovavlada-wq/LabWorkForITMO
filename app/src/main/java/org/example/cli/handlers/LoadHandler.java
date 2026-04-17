package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import java.util.Collection;
import java.util.List;

public class LoadHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params, SampleService sampleService,
                          MeasurementService measurementService, ProtocolService protocolService,
                          Collection<BaseHandler> commandList) {
        try {
            sampleService.loadData();
            measurementService.loadData();
            protocolService.loadData();
            System.out.println("OK All data loaded from JSON files");
            System.out.println("Loaded: " + sampleService.getAll().size() + " samples, " +
                    measurementService.getAll().size() + " measurements, " +
                    protocolService.getAll().size() + " protocols");
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String help() {
        return "load - load all data from JSON files";
    }
}