package org.example.services;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.validator.MeasurementValidator;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MeasurementService {
    private final List<Measurement> measurements = new ArrayList<>();
    private final SampleService sampleService;
    private long nextId = 1L;

    public MeasurementService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public Measurement add(long sampleId, MeasurementParam param, double value,
                           String unit, String method, String ownerUsername) {
        Sample sample = sampleService.getById(sampleId);

        if (sample.getStatus() != SampleStatus.ACTIVE) {
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
                Instant.now(),
                ownerUsername,
                Instant.now(),
                Instant.now()
        );

        measurements.add(measurement);
        return measurement;
    }

    public Measurement getById(long id) {
        return measurements.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ошибка: измерение с id=" + id + " не найдено"));
    }

    public List<Measurement> listBySample(long sampleId, Integer last) {
        List<Measurement> result = measurements.stream()
                .filter(m -> m.getSampleId() == sampleId)
                .sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed())
                .collect(Collectors.toList());

        if (last != null && last > 0) {
            return result.stream().limit(last).collect(Collectors.toList());
        }
        return result;
    }

    public List<Measurement> listBySample(long sampleId) {
        return listBySample(sampleId, null);
    }

    public List<Measurement> listByParam(MeasurementParam param, Integer last) {
        List<Measurement> result = measurements.stream()
                .filter(m -> m.getParam() == param)
                .sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed())
                .collect(Collectors.toList());

        if (last != null && last > 0) {
            return result.stream().limit(last).collect(Collectors.toList());
        }
        return result;
    }

    public void removeBySampleId(long sampleId) {
        measurements.removeIf(m -> m.getSampleId() == sampleId);
    }

    public boolean exists(long id) {
        return measurements.stream().anyMatch(m -> m.getId() == id);
    }

    public List<Measurement> getAll() {
        return measurements.stream()
                .sorted(Comparator.comparing(Measurement::getId))
                .collect(Collectors.toList());
    }
}