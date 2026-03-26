package org.example.cli.handlers;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.services.MeasurementService;
import org.example.services.SampleService;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class SampleUpdateHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params, SampleService sampleService, MeasurementService measurementService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public boolean handle(List<String> params, SampleService sampleService, Collection<BaseHandler> commandList) {
        try {
            if (params == null || params.isEmpty()) {
                System.out.println("Ошибка: не указан ID образца");
                return true;
            }

            long id;
            try {
                id = Long.parseLong(params.get(0));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: неверный формат ID");
                return true;
            }

            if (params.size() < 2) {
                System.out.println("Ошибка: не указаны поля для обновления");
                return true;
            }

            Sample sample = sampleService.getById(id);
            boolean updated = false;

            for (int i = 1; i < params.size(); i++) {
                String[] split = params.get(i).split("=", 2);
                if (split.length != 2) {
                    System.out.println("Ошибка: неверный формат параметра '" + params.get(i) + "'");
                    continue;
                }

                String key = split[0].toLowerCase();
                String value = split[1].trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                switch (key) {
                    case "name":
                        if (value.isEmpty()) {
                            System.out.println("Ошибка: name не может быть пустым");
                        } else if (value.length() > 128) {
                            System.out.println("Ошибка: название слишком длинное (макс. 128)");
                        } else {
                            sample.setName(value);
                            updated = true;
                        }
                        break;

                    case "type":
                        if (value.isEmpty()) {
                            System.out.println("Ошибка: type не может быть пустым");
                        } else if (value.length() > 64) {
                            System.out.println("Ошибка: тип слишком длинный (макс. 64)");
                        } else {
                            sample.setType(value);
                            updated = true;
                        }
                        break;

                    case "location":
                        if (value.isEmpty()) {
                            System.out.println("Ошибка: location не может быть пустым");
                        } else if (value.length() > 64) {
                            System.out.println("Ошибка: местоположение слишком длинное (макс. 64)");
                        } else {
                            sample.setLocation(value);
                            updated = true;
                        }
                        break;

                    case "status":
                        try {
                            SampleStatus newStatus = SampleStatus.valueOf(value.toUpperCase());
                            sample.setStatus(newStatus);
                            updated = true;
                        } catch (IllegalArgumentException e) {
                            System.out.println("Ошибка: статус только ACTIVE или ARCHIVED");
                        }
                        break;

                    default:
                        System.out.println("Ошибка: нельзя менять поле '" + key + "'");
                        break;
                }
            }

            if (updated) {
                sample.setUpdatedAt(Instant.now());
                System.out.println("OK");
            } else {
                System.out.println("Ничего не обновлено");
            }

        } catch (java.util.NoSuchElementException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String help() {
        return "sample_update <id> field=value ... - update sample fields (name, type, location, status)";
    }
}