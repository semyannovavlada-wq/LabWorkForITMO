package org.example.services;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Sample;
import org.example.validator.MeasurementValidator;

import java.time.Instant;
import java.util.*;

public class MeasurementService {
    private final TreeMap<Long, Measurement> measurements = new TreeMap<>();
    private final SampleService sampleService;
    private long nextId = 1L;

    public MeasurementService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public Measurement add(
            long sampleId,
            MeasurementParam param,
            double value,
            String unit,
            String method,
            String ownerUsername,
            Instant measuredAt
    ) {
        Sample sample = sampleService.getById(sampleId);
        if (sample == null) {
            throw new IllegalArgumentException("Ошибка: образец с id=" + sampleId + " не найден");
        }

        if (sample.getStatus() != org.example.domain.SampleStatus.ACTIVE) {
            throw new IllegalArgumentException("Ошибка: образец не активен, нельзя добавить измерение");
        }

        MeasurementValidator.validateParam(param);
        MeasurementValidator.validateValue(value);
        MeasurementValidator.validateUnit(unit);
        MeasurementValidator.validateMethod(method);
        MeasurementValidator.validateOwnerUsername(ownerUsername);

        Measurement measurement = new Measurement(
                nextId++,
                sampleId,
                param,
                value,
                unit,
                method,
                measuredAt != null ? measuredAt : Instant.now(),
                ownerUsername,
                Instant.now(),
                Instant.now()
        );

        measurements.put(measurement.getId(), measurement);
        return measurement;
    }

    public Measurement add(
            long sampleId,
            MeasurementParam param,
            double value,
            String unit,
            String method,
            String ownerUsername
    ) {
        return add(sampleId, param, value, unit, method, ownerUsername, Instant.now());
    }

    public Measurement getById(long id) {
        Measurement measurement = measurements.get(id);
        if (measurement == null) {
            throw new NoSuchElementException("Ошибка: измерение с id=" + id + " не найдено");
        }
        return measurement;
    }

    public List<Measurement> listBySample(long sampleId, Integer last) {
        Sample sample = sampleService.getById(sampleId);
        if (sample == null) {
            throw new IllegalArgumentException("Ошибка: образец с id=" + sampleId + " не найден");
        }

        List<Measurement> result = measurements.values().stream()
                .filter(m -> m.getSampleId() == sampleId)
                .sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed())
                .toList();

        if (last != null && last > 0) {
            return result.stream().limit(last).toList();
        }
        return result;
    }

    public List<Measurement> listBySample(long sampleId) {
        return listBySample(sampleId, null);
    }

    public List<Measurement> listByParam(MeasurementParam param, Integer last) {
        List<Measurement> result = measurements.values().stream()
                .filter(m -> m.getParam() == param)
                .sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed())
                .toList();

        if (last != null && last > 0) {
            return result.stream().limit(last).toList();
        }
        return result;
    }

    public Map<Long, Measurement> getBySampleId(long sampleId) {
        TreeMap<Long, Measurement> result = new TreeMap<>();
        measurements.values().stream()
                .filter(m -> m.getSampleId() == sampleId)
                .forEach(m -> result.put(m.getId(), m));
        return result;
    }

    public void removeBySampleId(long sampleId) {
        getBySampleId(sampleId).keySet().forEach(measurements::remove);
    }

    public boolean exists(long id) {
        return measurements.containsKey(id);
    }

    public List<Measurement> getAll() {
        return new ArrayList<>(measurements.values());
    }

    public Map<Long, Measurement> getAllAsMap() {
        return new TreeMap<>(measurements);
    }
}