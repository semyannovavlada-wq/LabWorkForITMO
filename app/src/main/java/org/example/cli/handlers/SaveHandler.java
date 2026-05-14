package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.StorageService;

import java.util.Collection;
import java.util.List;

public class SaveHandler implements BaseHandler {

    private final StorageService storageService;

    public SaveHandler(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          ProtocolService protocolService,
                          Collection<BaseHandler> commandList) {
        storageService.saveAll();
        return true;
    }

    @Override
    public String help() {
        return "save - Save all data to JSON files";
    }
}