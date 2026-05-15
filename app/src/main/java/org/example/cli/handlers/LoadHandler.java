package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.StorageService;

import java.util.Collection;
import java.util.List;

public class LoadHandler implements BaseHandler {

    private final StorageService storageService;

    public LoadHandler(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          ProtocolService protocolService,
                          Collection<BaseHandler> commandList) {
        storageService.loadAll();
        return true;
    }

    @Override
    public String help() {
        return "load - Load all data from JSON files";
    }
}