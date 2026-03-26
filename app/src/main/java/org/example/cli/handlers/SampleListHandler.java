package org.example.cli.handlers;

import org.example.cli.util.Param;
import org.example.domain.Sample;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public class SampleListHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params, SampleService sampleService, Collection<BaseHandler> commandList) {
        try {
            String statusFilter = Param.paramValue(params, "status");
            boolean mineOnly = Param.paramValue(params, "mine") != null;
            String currentUser = "SYSTEM";

            List<Sample> samples = sampleService.list(statusFilter, mineOnly, currentUser);

            if (samples.isEmpty()) {
                System.out.println("No samples found");
                return true;
            }

            // Заголовок таблицы
            System.out.println("ID Name Type Location Status");

            for (Sample sample : samples) {
                System.out.println(sample.getId() + " " +
                        sample.getName() + " " +
                        sample.getType() + " " +
                        sample.getLocation() + " " +
                        sample.getStatus());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String help() {
        return "sample_list [--status ACTIVE|ARCHIVED] [--mine] - list samples";
    }
}