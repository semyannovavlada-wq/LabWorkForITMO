package org.example.cli.handlers;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
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
    public boolean handle(List<String> params, SampleService sampleService, MeasurementService measurementService, ProtocolService protocolService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public boolean handle(List<String> params, SampleService sampleService, Collection<BaseHandler> commandList) {
        try {
            if (params == null || params.isEmpty()) {
                System.out.println("error: no ID ");
                return true;
            }

            long id;
            try {
                id = Long.parseLong(params.get(0));
            } catch (NumberFormatException e) {
                System.out.println("error: wrong ID");
                return true;
            }

            if (params.size() < 2) {
                System.out.println("error: no fields");
                return true;
            }

            Sample sample = sampleService.getById(id);
            boolean updated = false;

            for (int i = 1; i < params.size(); i++) {
                String[] split = params.get(i).split("=", 2);
                if (split.length != 2) {
                    System.out.println("error: wrong parameter format '" + params.get(i) + "'");
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
                            System.out.println("error: name cant be null");
                        } else if (value.length() > 128) {
                            System.out.println("error: name is too long (max. 128)");
                        } else {
                            sample.setName(value);
                            updated = true;
                        }
                        break;

                    case "type":
                        if (value.isEmpty()) {
                            System.out.println("error: type cant be null");
                        } else if (value.length() > 64) {
                            System.out.println("error: type is too long(max. 64)");
                        } else {
                            sample.setType(value);
                            updated = true;
                        }
                        break;

                    case "location":
                        if (value.isEmpty()) {
                            System.out.println("error: location can not be null");
                        } else if (value.length() > 64) {
                            System.out.println("error: location is too long (max. 64)");
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
                            System.out.println("error: ACTIVE or ARCHIVED");
                        }
                        break;

                    default:
                        System.out.println("error: cant change'" + key + "'");
                        break;
                }
            }

            if (updated) {
                sample.setUpdatedAt(Instant.now());
                System.out.println("OK");
            } else {
                System.out.println("no changes");
            }

        } catch (java.util.NoSuchElementException e) {
            System.out.println("error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String help() {
        return "SampleUpdate <id> field=value ... - update sample fields (name, type, location, status)";
    }
}