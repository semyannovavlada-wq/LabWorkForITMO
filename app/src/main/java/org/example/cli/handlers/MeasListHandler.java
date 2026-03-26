package org.example.cli.handlers;

import org.example.cli.util.Param;
import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Sample;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class MeasListHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          Collection<BaseHandler> commandList) {
        try {
            // Получаем ID образца
            if (params == null || params.isEmpty()) {
                System.out.println("error: ID ");
                return true;
            }

            long sampleId = Long.parseLong(params.get(0));

            // Проверяем существование образца
            Sample sample = sampleService.getById(sampleId);

            // Опциональный параметр --last N
            String lastStr = Param.paramValue(params, "last");
            int lastCount = lastStr != null ? Integer.parseInt(lastStr) : Integer.MAX_VALUE;

            // Получаем измерения образца
            List<Measurement> measurements = measurementService.listBySample(sampleId, lastCount);

            if (measurements.isEmpty()) {
                System.out.println("No measurements found");
                return true;
            }

            // Вывод таблицы
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            System.out.println("ID\tParam\t\tValue\tUnit\tMethod\tMeasuredAt");

            for (Measurement m : measurements) {
                System.out.println(m.getId() + "\t" +
                        m.getParam() + "\t" +
                        m.getValue() + "\t" +
                        m.getUnit() + "\t" +
                        m.getMethod() + "\t" +
                        m.getMeasuredAt().atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime().format(formatter));
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: wrong ID");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean handle(List<String> params, SampleService sampleService, MeasurementService measurementService, ProtocolService protocolService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public boolean handle(List<String> params, SampleService instrumentService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "MeasList <sample_id> [--last N] - list measurements for sample";
    }
}