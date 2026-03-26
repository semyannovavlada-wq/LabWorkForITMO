package org.example.cli.handlers;

import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public class HelpHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params, SampleService sampleService, Collection<BaseHandler> commandList) {
        for (BaseHandler handler : commandList) {
            System.out.println(handler.help());
        }
        return true;
    }

    @Override
    public boolean handle(List<String> params, SampleService sampleService, MeasurementService measurementService, ProtocolService protocolService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "help - list of commands";
    }
}