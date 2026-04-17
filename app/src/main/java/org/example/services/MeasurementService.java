package org.example.services;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.repository.MeasurementSaveLoad;
import org.example.validator.MeasurementValidator;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MeasurementService {
    private Map<Long, Measurement> measurements;
    private final SampleService sampleService;
    private long nextId;
    private final MeasurementSaveLoad storage;

    public MeasurementService(SampleService sampleService, String dataDir) {
        this.sampleService = sampleService;
        this.storage = new MeasurementSaveLoad(dataDir);
        this.measurements = new HashMap<>();
        this.nextId = 1L;
    }

    public MeasurementService(SampleService sampleService) {
        this(sampleService, "data");
    }

    public void loadData() {
        measurements = storage.load();
        if (measurements.isEmpty()) {
            measurements = new HashMap<>();
            nextId = 1L;
        } else {
            nextId = measurements.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
        }
    }

    public void saveData() {
        storage.save(measurements);
    }

    public Measurement add(long sampleId, MeasurementParam param, double value,
                           String unit, String method, String ownerUsername) {
        Sample sample = sampleService.getById(sampleId);

        if (sample.getStatus() != SampleStatus.ACTIVE) {
            throw new IllegalArgumentException("Error: cannot add measurement to ARCHIVED sample");
        }

        MeasurementValidator.validateParam(param);
        MeasurementValidator.validateValue(value);
        MeasurementValidator.validateUnit(unit);
        MeasurementValidator.validateMethod(method);
        MeasurementValidator.validateOwnerUsername(ownerUsername);

        long id = nextId++;
        Measurement measurement = new Measurement(id, sampleId, param, value, unit, method,
                Instant.now(), ownerUsername, Instant.now(), Instant.now());
        measurements.put(id, measurement);
        return measurement;
    }

    public Measurement getById(long id) {
        Measurement measurement = measurements.get(id);
        if (measurement == null) {
            throw new NoSuchElementException("Error: measurement with id=" + id + " not found");
        }
        return measurement;
    }

    public List<Measurement> listBySample(long sampleId, Integer last) {
        List<Measurement> result = measurements.values().stream()
                .filter(m -> m.getSampleId() == sampleId)
                .sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed())
                .collect(Collectors.toList());

        if (last != null && last > 0 && last < result.size()) {
            return result.subList(0, last);
        }
        return result;
    }

    public List<Measurement> listBySample(long sampleId) {
        return listBySample(sampleId, null);
    }

    public void removeBySampleId(long sampleId) {
        measurements.values().removeIf(m -> m.getSampleId() == sampleId);
    }

    public List<Measurement> getAll() {
        return new ArrayList<>(measurements.values());
    }

    public static class Stats {
        public final long count;
        public final double min;
        public final double max;
        public final double avg;

        public Stats(long count, double min, double max, double avg) {
            this.count = count;
            this.min = min;
            this.max = max;
            this.avg = avg;
        }
    }

    public Stats getStats(long sampleId, MeasurementParam param) {
        List<Measurement> filtered = measurements.values().stream()
                .filter(m -> m.getSampleId() == sampleId && m.getParam() == param)
                .toList();

        if (filtered.isEmpty()) {
            return new Stats(0, 0, 0, 0);
        }

        double min = filtered.stream().mapToDouble(Measurement::getValue).min().orElse(0);
        double max = filtered.stream().mapToDouble(Measurement::getValue).max().orElse(0);
        double avg = filtered.stream().mapToDouble(Measurement::getValue).average().orElse(0);

        return new Stats(filtered.size(), min, max, avg);
    }
}