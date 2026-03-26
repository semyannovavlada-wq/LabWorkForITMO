package org.example.domain;

import java.time.Instant;
import java.util.Set;

public final class Protocol {
    private long id;
    private String name;
    private Set<MeasurementParam> requiredParams;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Protocol(long id, String name, Set<MeasurementParam> requiredParams,
                    String ownerUsername, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.requiredParams = requiredParams;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
