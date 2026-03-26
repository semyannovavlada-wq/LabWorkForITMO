package org.example.cli.handlers;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import java.util.Collection;
import java.util.List;

public class MeasStatsHandler implements BaseHandler {

    @Override
    public boolean handle(List<String> params,
                          SampleService sampleService,
                          MeasurementService measurementService,
                          Collection<BaseHandler> commandList) {
        try {
            if (params == null || params.size() < 2) {
                System.out.println("Error: id and number");
                return true;
            }

            long sampleId = Long.parseLong(params.get(0));
            MeasurementParam param = MeasurementParam.valueOf(params.get(1).toUpperCase());

            List<Measurement> measurements = measurementService.listBySample(sampleId).stream()
                    .filter(m -> m.getParam() == param)
                    .toList();

            if (measurements.isEmpty()) {
                System.out.println("error: no measurments " + param + " for  sample=" + sampleId);
                return true;
            }

            double min = measurements.stream().mapToDouble(Measurement::getValue).min().orElse(0);
            double max = measurements.stream().mapToDouble(Measurement::getValue).max().orElse(0);
            double avg = measurements.stream().mapToDouble(Measurement::getValue).average().orElse(0);

            System.out.println("count: " + measurements.size());
            System.out.println("min: " + min);
            System.out.println("max: " + max);
            System.out.println("avg: " + avg);

        } catch (NumberFormatException e) {
            System.out.println("error: ID");
        } catch (IllegalArgumentException e) {
            System.out.println("error: PH, CONDUCTIVITY, TURBIDITY, NITRATE");
        } catch (java.util.NoSuchElementException e) {
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
    public boolean handle(List<String> params, SampleService instrumentService, Collection<BaseHandler> commandList) {
        return false;
    }

    @Override
    public String help() {
        return "MeasStat <sample_id> <param> - show statistics for parameter";
    }
}