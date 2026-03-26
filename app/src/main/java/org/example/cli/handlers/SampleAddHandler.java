package org.example.cli.handlers;

import org.example.cli.services.ReaderService;
import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public class SampleAddHandler implements BaseHandler {
    private final ReaderService readerService = new ReaderService();

    @Override
    public boolean handle(List<String> params, SampleService sampleService, Collection<BaseHandler> commandList) {
        try {
            // Ввод названия
            System.out.println("name:");
            String name = String.join("", readerService.readCommand()).trim();
            if (name.isEmpty()) {
                System.out.println("error: cant be null");
                return true;
            }
            if (name.length() > 128) {
                System.out.println("error: too long (max 128 )");
                return true;
            }

            // Ввод типа
            System.out.println("type:");
            String type = String.join("", readerService.readCommand()).trim();
            if (type.isEmpty()) {
                System.out.println("error: type can not be empty");
                return true;
            }
            if (type.length() > 64) {
                System.out.println("error type is too long (max. 64 )");
                return true;
            }

            // Ввод местоположения
            System.out.println("location:");
            String location = String.join("", readerService.readCommand()).trim();
            if (location.isEmpty()) {
                System.out.println("error: location can not be null");
                return true;
            }
            if (location.length() > 64) {
                System.out.println("error: location is way too long (max. 64 )");
                return true;
            }

            // Создание образца
            Sample sample = sampleService.add(name, type, location, "SYSTEM", SampleStatus.ACTIVE);
            System.out.println("OK sample_id=" + sample.getId());

        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean handle(List<String> params, SampleService sampleService, MeasurementService measurementService, ProtocolService protocolService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "SampleAdd - создать новый образец (интерактивно)";
    }
}