package org.example.storage.saveLoad;

import java.util.Collection;
import java.util.Map;

public interface SaveLoad<T, ID> {
    void save(Collection<T> entities);
    Map<ID, T> load();
    boolean exists();
}