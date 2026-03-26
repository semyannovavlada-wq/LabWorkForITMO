package org.example.cli.handlers;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class SampleArchiveHandler implements BaseHandler {

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

            Sample sample = sampleService.getById(id);

            if (sample.getStatus() == SampleStatus.ARCHIVED) {
                System.out.println("error: already ARCHIVED");
                return true;
            }

            sample.setStatus(SampleStatus.ARCHIVED);
            sample.setUpdatedAt(Instant.now());

            System.out.println("OK sample " + id + " archived");

        } catch (java.util.NoSuchElementException e) {
            System.out.println("error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String help() {
        return "SampleArchive <id> - archive sample";
    }
}