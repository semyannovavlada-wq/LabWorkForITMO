package org.example.services;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import org.example.domain.Protocol;
import org.example.repository.ProtocolSaveLoad;
import org.example.validator.ProtocolValidator;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ProtocolService {
    private Map<Long, Protocol> protocols;
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private long nextId;
    private final ProtocolSaveLoad storage;

    public ProtocolService(SampleService sampleService, MeasurementService measurementService, String dataDir) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.storage = new ProtocolSaveLoad(dataDir);
        this.protocols = new HashMap<>();
        this.nextId = 1L;
    }

    public ProtocolService(SampleService sampleService, MeasurementService measurementService) {
        this(sampleService, measurementService, "data");
    }

    public void loadData() {
        protocols = storage.load();
        if (protocols.isEmpty()) {
            protocols = new HashMap<>();
            nextId = 1L;
        } else {
            nextId = protocols.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
        }
    }

    public void saveData() {
        storage.save(protocols);
    }

    public Protocol add(String name, Set<MeasurementParam> requiredParams, String ownerUsername) {
        ProtocolValidator.validateName(name);
        ProtocolValidator.validateRequiredParams(requiredParams);
        ProtocolValidator.validateOwnerUsername(ownerUsername);

        long id = nextId++;
        Protocol protocol = new Protocol(id, name, requiredParams, ownerUsername, Instant.now(), Instant.now());
        protocols.put(id, protocol);
        return protocol;
    }

    public Protocol getById(long id) {
        Protocol protocol = protocols.get(id);
        if (protocol == null) {
            throw new NoSuchElementException("Error: protocol with id=" + id + " not found");
        }
        return protocol;
    }

    public List<Protocol> getAll() {
        return new ArrayList<>(protocols.values());
    }

    public List<Protocol> list(boolean mineOnly, String currentUser) {
        return protocols.values().stream()
                .filter(p -> {
                    if (mineOnly && currentUser != null) {
                        return p.getOwnerUsername().equals(currentUser);
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Protocol::getId))
                .toList();
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
            throw new SecurityException("Error: no rights to delete this protocol");
        }
        protocols.remove(id);
        return true;
    }

    public boolean exists(long id) {
        return protocols.containsKey(id);
    }
}