package org.example.cli.handlers;

import org.example.cli.services.ReaderService;
import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Sample;
import org.example.services.MeasurementService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SampleShowHandler implements BaseHandler {
    private final ReaderService readerService = new ReaderService();

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          Collection<BaseHandler> commandList) {
        try {
            long id;

            // Если параметр передан в командной строке
            if (params != null && !params.isEmpty()) {
                try {
                    id = Long.parseLong(params.get(0));
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: неверный формат ID");
                    return true;
                }
            } else {
                // Интерактивный ввод ID
                System.out.println("Id - ?");
                String input = String.join("", readerService.readCommand()).trim();
                try {
                    id = Long.parseLong(input);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: неверный формат ID");
                    return true;
                }
            }

            // Получаем образец
            Sample sample = sampleService.getById(id);

            // Получаем измерения образца
            List<Measurement> measurements = measurementService.listBySample(id);

            // Собираем уникальные параметры измерений
            Set<MeasurementParam> paramsSet = measurements.stream()
                    .map(Measurement::getParam)
                    .collect(Collectors.toSet());

            // Формируем строку с параметрами
            String paramsStr = paramsSet.stream()
                    .map(MeasurementParam::name)
                    .collect(Collectors.joining(", "));

            // Вывод карточки образца
            System.out.println("Sample #" + sample.getId());
            System.out.println("name: " + sample.getName());
            System.out.println("type: " + sample.getType());
            System.out.println("location: " + sample.getLocation());
            System.out.println("status: " + sample.getStatus());
            System.out.println("owner: " + sample.getOwnerUsername());
            System.out.println("measurements: " + measurements.size());
            System.out.println("params: " + (paramsStr.isEmpty() ? "—" : paramsStr));

        } catch (java.util.NoSuchElementException e) {
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
        return "sample_show <id> - show sample profile and measurements statistics";
    }
}