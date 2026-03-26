package org.example.domain;

import java.time.Instant;

public final class Measurement {
    private long id;
    private long sampleId;
    private MeasurementParam param;
    private double value;
    private String unit;
    private String method;
    private Instant measuredAt;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Measurement(long id, long sampleId, MeasurementParam param, double value,
                       String unit, String method, Instant measuredAt, String ownerUsername,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sampleId = sampleId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.method = method;
        this.measuredAt = measuredAt;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
