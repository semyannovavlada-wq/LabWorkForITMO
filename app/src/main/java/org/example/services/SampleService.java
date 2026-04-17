package org.example.services;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.repository.SampleSaveLoad;
import org.example.validator.SampleValidator;
import java.time.Instant;
import java.util.*;

public class SampleService {
    private Map<Long, Sample> samples;
    private long nextId;
    private final SampleSaveLoad storage;

    public SampleService(String dataDir) {
        this.storage = new SampleSaveLoad(dataDir);
        this.samples = new HashMap<>();
        this.nextId = 1L;
    }

    public SampleService() {
        this("data");
    }

    public void loadData() {
        samples = storage.load();
        if (samples.isEmpty()) {
            samples = new HashMap<>();
            nextId = 1L;
        } else {
            nextId = samples.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
        }
    }

    public void saveData() {
        storage.save(samples);
    }

    public Sample add(String name, String type, String location, String ownerUsername, SampleStatus status) {
        SampleValidator.validateName(name);
        SampleValidator.validateType(type);
        SampleValidator.validateLocation(location);
        SampleValidator.validateOwnerUsername(ownerUsername);

        long id = nextId++;
        Sample sample = new Sample(id, name, type, location,
                status != null ? status : SampleStatus.ACTIVE,
                ownerUsername, Instant.now(), Instant.now());
        samples.put(id, sample);
        return sample;
    }

    public Sample add(String name, String type, String location, String ownerUsername) {
        return add(name, type, location, ownerUsername, SampleStatus.ACTIVE);
    }

    public Sample getById(long id) {
        Sample sample = samples.get(id);
        if (sample == null) {
            throw new NoSuchElementException("Error: sample with id=" + id + " not found");
        }
        return sample;
    }

    public List<Sample> getAll() {
        return new ArrayList<>(samples.values());
    }

    public List<Sample> list(String statusFilter, boolean mineOnly, String currentUser) {
        return samples.values().stream()
                .filter(s -> {
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        try {
                            SampleStatus status = SampleStatus.valueOf(statusFilter.toUpperCase());
                            if (s.getStatus() != status) return false;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    if (mineOnly && currentUser != null) {
                        if (!s.getOwnerUsername().equals(currentUser)) return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Sample::getId))
                .toList();
    }

    public Sample updateStatus(long id, SampleStatus newStatus, String ownerUsername) {
        Sample sample = getById(id);
        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Error: no rights to modify this sample");
        }
        sample.setStatus(newStatus);
        sample.setUpdatedAt(Instant.now());
        return sample;
    }

    public void update(Sample sample) {
        samples.put(sample.getId(), sample);
    }

    public boolean remove(long id, String ownerUsername) {
        Sample sample = getById(id);
        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Error: no rights to delete this sample");
        }
        if (sample.getStatus() != SampleStatus.ARCHIVED) {
            throw new IllegalStateException("Error: only ARCHIVED samples can be deleted");
        }
        samples.remove(id);
        return true;
    }

    public boolean exists(long id) {
        return samples.containsKey(id);
    }
}