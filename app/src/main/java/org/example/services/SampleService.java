package org.example.services;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.validator.SampleValidator;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SampleService {
    private final TreeMap<Long, Sample> samples = new TreeMap<>();
    private long nextId = 1L;

    // Добавление образца
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

        samples.put(sample.getId(), sample);
        return sample;
    }

    // Добавление образца со статусом по умолчанию ACTIVE
    public Sample add(String name, String type, String location, String ownerUsername) {
        return add(name, type, location, ownerUsername, SampleStatus.ACTIVE);
    }

    // Получить образец по ID
    public Sample getById(long id) {
        Sample sample = samples.get(id);
        if (sample == null) {
            throw new NoSuchElementException("Ошибка: образец с id=" + id + " не найден");
        }
        return sample;
    }

    // Получить все образцы
    public List<Sample> getAll() {
        return new ArrayList<>(samples.values());
    }

    // Список образцов с фильтрацией
    public List<Sample> list(String statusFilter, boolean mineOnly, String currentUser) {
        return samples.values().stream()
                .filter(sample -> {
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        try {
                            SampleStatus status = SampleStatus.valueOf(statusFilter.toUpperCase());
                            if (sample.getStatus() != status) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            // Если статус не распознан, пропускаем
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

    // Обновить статус образца
    public Sample updateStatus(long id, SampleStatus newStatus, String ownerUsername) {
        Sample sample = getById(id);

        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на изменение этого образца");
        }

        sample.setStatus(newStatus);
        sample.setUpdatedAt(Instant.now());
        return sample;
    }

    // Обновить местоположение образца
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

    // Удалить образец (только если ARCHIVED)
    public boolean remove(long id, String ownerUsername) {
        Sample sample = getById(id);

        if (!sample.getOwnerUsername().equals(ownerUsername) && !"SYSTEM".equals(ownerUsername)) {
            throw new SecurityException("Ошибка: нет прав на удаление этого образца");
        }

        if (sample.getStatus() != SampleStatus.ARCHIVED) {
            throw new IllegalStateException("Ошибка: можно удалить только образец со статусом ARCHIVED");
        }

        return samples.remove(id) != null;
    }

    // Проверка существования образца
    public boolean exists(long id) {
        return samples.containsKey(id);
    }

    // Получить образцы по владельцу
    public List<Sample> getByOwner(String ownerUsername) {
        return samples.values().stream()
                .filter(sample -> sample.getOwnerUsername().equals(ownerUsername))
                .collect(Collectors.toList());
    }

    // Получить образцы по статусу
    public List<Sample> getByStatus(SampleStatus status) {
        return samples.values().stream()
                .filter(sample -> sample.getStatus() == status)
                .collect(Collectors.toList());
    }

    // Получить все образцы как Map (id -> Sample)
    public Map<Long, Sample> getAllAsMap() {
        return new TreeMap<>(samples);
    }
}