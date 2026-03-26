package org.example.cli.handlers;

import org.example.cli.services.ReaderService;
import org.example.domain.MeasurementParam;
import org.example.domain.Protocol;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtCreateHandler implements BaseHandler {
    private final ReaderService readerService = new ReaderService();

    @Override
    public boolean handle(List<String> params,
                          ProtocolService protocolService,
                          Collection<BaseHandler> commandList) {
        try {
            System.out.println("Название протокола:");
            String name = String.join("", readerService.readCommand()).trim();

            if (name.isEmpty()) {
                System.out.println("Ошибка: имя протокола не может быть пустым");
                return true;
            }

            System.out.println("Обязательные параметры (через запятую):");
            String paramsInput = String.join("", readerService.readCommand()).trim();

            if (paramsInput.isEmpty()) {
                System.out.println("Ошибка: нужно указать хотя бы один параметр");
                return true;
            }

            Set<MeasurementParam> requiredParams = Arrays.stream(paramsInput.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(MeasurementParam::valueOf)
                    .collect(Collectors.toSet());

            Protocol protocol = protocolService.add(name, requiredParams, "SYSTEM");
            System.out.println("OK protocol_id=" + protocol.getId());

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: неверный параметр. Доступные: PH, CONDUCTIVITY, TURBIDITY, NITRATE");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean handle(List<String> params, SampleService sampleService, MeasurementService measurementService, ProtocolService protocolService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "ProtCreate - create protocol";
    }
}