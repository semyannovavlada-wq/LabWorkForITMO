package org.example.cli.handlers;

import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public class ExitHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "exit - for exit";
    }
}