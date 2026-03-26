package org.example.services;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Protocol;
import org.example.validator.ProtocolValidator;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ProtocolService {
    private final List<Protocol> protocols = new ArrayList<>();
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private long nextId = 1L;

    public ProtocolService(SampleService sampleService, MeasurementService measurementService) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
    }

    public Protocol add(String name, Set<MeasurementParam> requiredParams, String ownerUsername) {
        ProtocolValidator.validateName(name);
        ProtocolValidator.validateRequiredParams(requiredParams);
        ProtocolValidator.validateOwnerUsername(ownerUsername);

        Protocol protocol = new Protocol(
                nextId++,
                name,
                requiredParams,
                ownerUsername,
                Instant.now(),
                Instant.now()
        );

        protocols.add(protocol);
        return protocol;
    }

    public Protocol getById(long id) {
        return protocols.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ошибка: протокол с id=" + id + " не найден"));
    }

    public List<Protocol> getAll() {
        return protocols.stream()
                .sorted(Comparator.comparing(Protocol::getId))
                .collect(Collectors.toList());
    }

    public List<Protocol> list(boolean mineOnly, String currentUser) {
        return protocols.stream()
                .filter(protocol -> {
                    if (mineOnly && currentUser != null && !currentUser.isEmpty()) {
                        return protocol.getOwnerUsername().equals(currentUser);
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Protocol::getId))
                .collect(Collectors.toList());
    }

    public List<Protocol> getByOwner(String ownerUsername) {
        return protocols.stream()
                .filter(protocol -> protocol.getOwnerUsername().equals(ownerUsername))
                .sorted(Comparator.comparing(Protocol::getId))
                .collect(Collectors.toList());
    }

    public Protocol updateName(long id, String newName, String ownerUsername) {
        Protocol protocol = getById(id);

        if (!protocol.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на изменение этого протокола");
        }

        ProtocolValidator.validateName(newName);
        protocol.setName(newName);
        protocol.setUpdatedAt(Instant.now());
        return protocol;
    }

    public Protocol updateRequiredParams(long id, Set<MeasurementParam> newRequiredParams, String ownerUsername) {
        Protocol protocol = getById(id);

        if (!protocol.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на изменение этого протокола");
        }

        ProtocolValidator.validateRequiredParams(newRequiredParams);
        protocol.setRequiredParams(newRequiredParams);
        protocol.setUpdatedAt(Instant.now());
        return protocol;
    }

    public boolean checkSampleCompliance(long sampleId, long protocolId) {
        Protocol protocol = getById(protocolId);
        sampleService.getById(sampleId);

        List<Measurement> measurements = measurementService.listBySample(sampleId);
        Set<MeasurementParam> measuredParams = measurements.stream()
                .map(Measurement::getParam)
                .collect(Collectors.toSet());

        return measuredParams.containsAll(protocol.getRequiredParams());
    }

    public Set<MeasurementParam> getMissingParams(long sampleId, long protocolId) {
        Protocol protocol = getById(protocolId);
        List<Measurement> measurements = measurementService.listBySample(sampleId);

        Set<MeasurementParam> measuredParams = measurements.stream()
                .map(Measurement::getParam)
                .collect(Collectors.toSet());

        Set<MeasurementParam> missing = new HashSet<>(protocol.getRequiredParams());
        missing.removeAll(measuredParams);
        return missing;
    }

    public boolean remove(long id, String ownerUsername) {
        Protocol protocol = getById(id);

        if (!protocol.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на удаление этого протокола");
        }

        return protocols.remove(protocol);
    }

    public boolean exists(long id) {
        return protocols.stream().anyMatch(p -> p.getId() == id);
    }
}