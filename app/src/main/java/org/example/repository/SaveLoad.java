package org.example.repository;

import java.util.Map;

public interface SaveLoad<T, ID> {
    void save(Map<ID, T> entities);
    Map<ID, T> load();
    boolean exists();
}