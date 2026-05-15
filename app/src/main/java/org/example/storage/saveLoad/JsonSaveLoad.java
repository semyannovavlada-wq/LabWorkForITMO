package org.example.storage.saveLoad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.storage.saveLoad.InstantAdapter;
import java.time.Instant;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class JsonSaveLoad<T, ID> implements SaveLoad<T, ID> {

    private final Path filePath;
    private final Function<T, ID> extractId;
    private final Type listType;
    private final Gson gson;

    public JsonSaveLoad(Path filePath, Function<T, ID> extractId, Type listType) {
        this.filePath = filePath;
        this.extractId = extractId;
        this.listType = listType;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void save(Collection<T> entities) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            String json = gson.toJson(entities);
            Files.writeString(filePath, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error saving to " + filePath + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Map<ID, T> load() {
        if (!Files.exists(filePath)) {
            return Collections.emptyMap();
        }
        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8);
            List<T> list = gson.fromJson(json, listType);
            Map<ID, T> result = new LinkedHashMap<>();
            for (T entity : list) {
                result.put(extractId.apply(entity), entity);
            }
            return result;
        } catch (IOException e) {
            System.err.println("Error loading from " + filePath + ": " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(filePath);
    }
}