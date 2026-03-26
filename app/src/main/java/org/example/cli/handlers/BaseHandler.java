package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.SampleService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public interface BaseHandler {

    boolean handle(List<String> params,
                   SampleService sampleService,
                   MeasurementService measurementService,
                   Collection<BaseHandler> commandList);

    boolean handle(List<String> params,
                   SampleService instrumentService,
                   Collection<BaseHandler> commandList);

    String help();
}