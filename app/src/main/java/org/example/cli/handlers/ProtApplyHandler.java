package org.example.cli.handlers;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Protocol;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtApplyHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          ProtocolService protocolService,
                          Collection<BaseHandler> commandList) {
        try {
            if (params == null || params.size() < 2) {
                System.out.println("error: no protocol ID and no sample ID");
                return true;
            }

            long protocolId = Long.parseLong(params.get(0));
            long sampleId = Long.parseLong(params.get(1));

            Protocol protocol = protocolService.getById(protocolId);
            sampleService.getById(sampleId); // проверка существования образца

            Set<MeasurementParam> measuredParams = measurementService.listBySample(sampleId).stream()
                    .map(Measurement::getParam)
                    .collect(Collectors.toSet());

            Set<MeasurementParam> missingParams = protocol.getRequiredParams().stream()
                    .filter(param -> !measuredParams.contains(param))
                    .collect(Collectors.toSet());

            if (missingParams.isEmpty()) {
                System.out.println("OK protocol is complete");
            } else {
                System.out.println("Missing params: " + missingParams.stream()
                        .map(MeasurementParam::name)
                        .collect(Collectors.joining(", ")));
            }

        } catch (NumberFormatException e) {
            System.out.println("error: ID");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String help() {
        return "ProtApply <protocol_id> <sample_id> - check protocol completeness";
    }
}