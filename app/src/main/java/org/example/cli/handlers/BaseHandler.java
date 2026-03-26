package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public interface BaseHandler {

    // Для хендлеров, которым не нужны сервисы (ExitHandler, HelpHandler)
    default boolean handle(List<String> params,
                           Collection<BaseHandler> commandList) {
        throw new UnsupportedOperationException("Not supported");
    }

    // Для хендлеров, которым нужен только SampleService
    default boolean handle(List<String> params,
                           SampleService sampleService,
                           Collection<BaseHandler> commandList) {
        throw new UnsupportedOperationException("Not supported");
    }

    // Для хендлеров, которым нужен только ProtocolService
    default boolean handle(List<String> params,
                           ProtocolService protocolService,
                           Collection<BaseHandler> commandList) {
        throw new UnsupportedOperationException("Not supported");
    }

    // Для хендлеров, которым нужны SampleService + MeasurementService
    default boolean handle(List<String> params,
                           SampleService sampleService,
                           MeasurementService measurementService,
                           Collection<BaseHandler> commandList) {
        throw new UnsupportedOperationException("Not supported");
    }

    boolean handle(List<String> params,
                   SampleService sampleService,
                   MeasurementService measurementService,
                   ProtocolService protocolService,
                   Collection<BaseHandler> commandList);

    String help();
}