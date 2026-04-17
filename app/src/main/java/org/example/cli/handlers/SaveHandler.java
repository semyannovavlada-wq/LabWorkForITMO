package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import java.util.Collection;
import java.util.List;

public class SaveHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params, SampleService sampleService,
                          MeasurementService measurementService, ProtocolService protocolService,
                          Collection<BaseHandler> commandList) {
        try {
            sampleService.saveData();
            measurementService.saveData();
            protocolService.saveData();
            System.out.println("OK All data saved to JSON files");
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String help() {
        return "save - save all data to JSON files";
    }
}