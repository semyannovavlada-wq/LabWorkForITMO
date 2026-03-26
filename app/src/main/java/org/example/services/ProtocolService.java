package org.example.services;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Protocol;
import org.example.domain.Sample;
import org.example.validator.ProtocolValidator;

import java.time.Instant;
import java.util.*;

public class ProtocolService {
    private final TreeMap<Long, Protocol> protocols = new TreeMap<>();
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private long nextId = 1L;

    public ProtocolService(SampleService sampleService, MeasurementService measurementService) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
    }

    // Добавление протокола
    public Protocol add(
            String name,
            Set<MeasurementParam> requiredParams,
            String ownerUsername
    ) {
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

        protocols.put(protocol.getId(), protocol);
        return protocol;
    }

    // Получить протокол по ID
    public Protocol getById(long id) {
        Protocol protocol = protocols.get(id);
        if (protocol == null) {
            throw new NoSuchElementException("Ошибка: протокол с id=" + id + " не найден");
        }
        return protocol;
    }

    // Получить все протоколы
    public List<Protocol> getAll() {
        return new ArrayList<>(protocols.values());
    }

    // Список протоколов с фильтрацией по владельцу
    public List<Protocol> list(boolean mineOnly, String currentUser) {
        return protocols.values().stream()
                .filter(protocol -> {
                    if (mineOnly && currentUser != null && !currentUser.isEmpty()) {
                        return protocol.getOwnerUsername().equals(currentUser);
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Protocol::getId))
                .toList();
    }

    // Список протоколов по владельцу
    public List<Protocol> getByOwner(String ownerUsername) {
        return protocols.values().stream()
                .filter(protocol -> protocol.getOwnerUsername().equals(ownerUsername))
                .toList();
    }

    // Обновить название протокола
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

    // Обновить список обязательных параметров
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

    // Проверить, соответствует ли образец протоколу
    public boolean checkSampleCompliance(long sampleId, long protocolId) {
        Protocol protocol = getById(protocolId);
        Sample sample = sampleService.getById(sampleId);

        if (sample == null) {
            throw new IllegalArgumentException("Ошибка: образец с id=" + sampleId + " не найден");
        }

        List<Measurement> measurements = measurementService.listBySample(sampleId);
        Set<MeasurementParam> measuredParams = measurements.stream()
                .map(Measurement::getParam)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        return measuredParams.containsAll(protocol.getRequiredParams());
    }

    // Получить отсутствующие параметры для образца по протоколу
    public Set<MeasurementParam> getMissingParams(long sampleId, long protocolId) {
        Protocol protocol = getById(protocolId);
        List<Measurement> measurements = measurementService.listBySample(sampleId);

        Set<MeasurementParam> measuredParams = measurements.stream()
                .map(Measurement::getParam)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        Set<MeasurementParam> missing = new HashSet<>(protocol.getRequiredParams());
        missing.removeAll(measuredParams);

        return missing;
    }

    // Удалить протокол
    public boolean remove(long id, String ownerUsername) {
        Protocol protocol = getById(id);

        if (!protocol.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на удаление этого протокола");
        }

        return protocols.remove(id) != null;
    }

    // Проверка существования протокола
    public boolean exists(long id) {
        return protocols.containsKey(id);
    }

    // Получить все протоколы как Map
    public Map<Long, Protocol> getAllAsMap() {
        return new TreeMap<>(protocols);
    }
}