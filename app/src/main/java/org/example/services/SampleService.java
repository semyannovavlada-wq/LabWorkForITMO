package org.example.services;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.validator.SampleValidator;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SampleService {
    private final List<Sample> samples = new ArrayList<>();
    private long nextId = 1L;

    public Sample add(String name, String type, String location,
                      String ownerUsername, SampleStatus status) {
        SampleValidator.validateName(name);
        SampleValidator.validateType(type);
        SampleValidator.validateLocation(location);
        SampleValidator.validateOwnerUsername(ownerUsername);

        Sample sample = new Sample(
                nextId++,
                name,
                type,
                location,
                status != null ? status : SampleStatus.ACTIVE,
                ownerUsername,
                Instant.now(),
                Instant.now()
        );

        samples.add(sample);
        return sample;
    }

    public Sample add(String name, String type, String location, String ownerUsername) {
        return add(name, type, location, ownerUsername, SampleStatus.ACTIVE);
    }

    public Sample getById(long id) {
        return samples.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ошибка: образец с id=" + id + " не найден"));
    }

    public List<Sample> getAll() {
        return samples.stream()
                .sorted(Comparator.comparing(Sample::getId))
                .collect(Collectors.toList());
    }

    public List<Sample> list(String statusFilter, boolean mineOnly, String currentUser) {
        return samples.stream()
                .filter(sample -> {
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        try {
                            SampleStatus status = SampleStatus.valueOf(statusFilter.toUpperCase());
                            if (sample.getStatus() != status) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    if (mineOnly && currentUser != null && !currentUser.isEmpty()) {
                        if (!sample.getOwnerUsername().equals(currentUser)) {
                            return false;
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Sample::getId))
                .collect(Collectors.toList());
    }

    public Sample updateStatus(long id, SampleStatus newStatus, String ownerUsername) {
        Sample sample = getById(id);

        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на изменение этого образца");
        }

        sample.setStatus(newStatus);
        sample.setUpdatedAt(Instant.now());
        return sample;
    }

    public Sample updateLocation(long id, String newLocation, String ownerUsername) {
        Sample sample = getById(id);

        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на изменение этого образца");
        }

        SampleValidator.validateLocation(newLocation);
        sample.setLocation(newLocation);
        sample.setUpdatedAt(Instant.now());
        return sample;
    }

    public boolean remove(long id, String ownerUsername) {
        Sample sample = getById(id);

        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на удаление этого образца");
        }

        if (sample.getStatus() != SampleStatus.ARCHIVED) {
            throw new IllegalStateException("Ошибка: можно удалить только образец со статусом ARCHIVED");
        }

        return samples.remove(sample);
    }

    public boolean exists(long id) {
        return samples.stream().anyMatch(s -> s.getId() == id);
    }

    public List<Sample> getByOwner(String ownerUsername) {
        return samples.stream()
                .filter(sample -> sample.getOwnerUsername().equals(ownerUsername))
                .sorted(Comparator.comparing(Sample::getId))
                .collect(Collectors.toList());
    }

    public List<Sample> getByStatus(SampleStatus status) {
        return samples.stream()
                .filter(sample -> sample.getStatus() == status)
                .sorted(Comparator.comparing(Sample::getId))
                .collect(Collectors.toList());
    }
}