package org.example.cli.handlers;

import org.example.cli.services.ReaderService;
import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.services.MeasurementService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public class MeasAddHandler implements BaseHandler {
    private final ReaderService readerService = new ReaderService();

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          Collection<BaseHandler> commandList) {
        try {
            if (params == null || params.isEmpty()) {
                System.out.println("Ошибка: не указан ID образца");
                return true;
            }

            long sampleId;
            try {
                sampleId = Long.parseLong(params.get(0));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: неверный формат ID");
                return true;
            }

            Sample sample = sampleService.getById(sampleId);

            if (sample.getStatus() != SampleStatus.ACTIVE) {
                System.out.println("Ошибка: нельзя добавлять измерения к ARCHIVED образцу");
                return true;
            }

            // Параметр
            System.out.println("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE):");
            MeasurementParam param;
            try {
                param = MeasurementParam.valueOf(String.join("", readerService.readCommand()).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный параметр");
                return true;
            }

            // Значение
            System.out.println("Значение:");
            double value;
            try {
                value = Double.parseDouble(String.join("", readerService.readCommand()).trim());
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    System.out.println("Ошибка: значение должно быть числом");
                    return true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: значение должно быть числом");
                return true;
            }

            // Единицы
            System.out.println("Единицы:");
            String unit = String.join("", readerService.readCommand()).trim();
            if (unit.isEmpty()) {
                System.out.println("Ошибка: единицы не могут быть пустыми");
                return true;
            }

            // Метод
            System.out.println("Метод:");
            String method = String.join("", readerService.readCommand()).trim();
            if (method.isEmpty()) {
                System.out.println("Ошибка: метод не может быть пустым");
                return true;
            }

            Measurement measurement = measurementService.add(sampleId, param, value, unit, method, "SYSTEM");
            System.out.println("OK measurement_id=" + measurement.getId());

        } catch (java.util.NoSuchElementException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean handle(List<String> params, SampleService instrumentService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "meas_add <sample_id> - add measurement to sample";
    }
}