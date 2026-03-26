package org.example.domain;

import java.time.Instant;

public final class Sample {
    private long id;
    private String name;
    private String type;
    private String location;
    private SampleStatus status;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Sample(long id, String name, String type, String location,
                  SampleStatus status, String ownerUsername, Instant createdAt,
                  Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
