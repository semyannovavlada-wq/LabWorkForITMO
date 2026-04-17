package org.example.repository;

import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class SampleSaveLoad implements SaveLoad<Sample, Long> {
    private final File file;

    public SampleSaveLoad(String dataDir) {
        this.file = new File(dataDir, "samples.json");
        ensureDirectoryExists(dataDir);
    }

    private void ensureDirectoryExists(String dataDir) {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String toJson(Sample sample) {
        return "{" +
                "\"id\": " + sample.getId() + "," +
                "\"name\": \"" + JsonUtils.escape(sample.getName()) + "\"," +
                "\"type\": \"" + JsonUtils.escape(sample.getType()) + "\"," +
                "\"location\": \"" + JsonUtils.escape(sample.getLocation()) + "\"," +
                "\"status\": \"" + sample.getStatus().name() + "\"," +
                "\"owner\": \"" + JsonUtils.escape(sample.getOwnerUsername()) + "\"," +
                "\"createdAt\": \"" + sample.getCreatedAt().toString() + "\"," +
                "\"updatedAt\": \"" + sample.getUpdatedAt().toString() + "\"" +
                "}";
    }

    private Sample fromJson(String json) {
        try {
            long id = JsonUtils.extractLong(json, "id");
            String name = JsonUtils.extractString(json, "name");
            String type = JsonUtils.extractString(json, "type");
            String location = JsonUtils.extractString(json, "location");
            SampleStatus status = SampleStatus.valueOf(JsonUtils.extractString(json, "status"));
            String owner = JsonUtils.extractString(json, "owner");
            Instant createdAt = Instant.parse(JsonUtils.extractString(json, "createdAt"));
            Instant updatedAt = Instant.parse(JsonUtils.extractString(json, "updatedAt"));

            return new Sample(id, name, type, location, status, owner, createdAt, updatedAt);
        } catch (Exception e) {
            System.err.println("Error parsing Sample JSON: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Map<Long, Sample> samples) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("[");
            List<Sample> list = new ArrayList<>(samples.values());
            for (int i = 0; i < list.size(); i++) {
                writer.print("  ");
                writer.print(toJson(list.get(i)));
                if (i < list.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");
        } catch (IOException e) {
            System.err.println("Error saving samples: " + e.getMessage());
        }
    }

    @Override
    public Map<Long, Sample> load() {
        Map<Long, Sample> result = new HashMap<>();
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String json = content.toString();
            int start = json.indexOf("{");
            while (start != -1) {
                int end = JsonUtils.findMatchingBrace(json, start);
                if (end == -1) break;

                String sampleJson = json.substring(start, end + 1);
                Sample sample = fromJson(sampleJson);
                if (sample != null) {
                    result.put(sample.getId(), sample);
                }
                start = json.indexOf("{", end + 1);
            }
        } catch (IOException e) {
            System.err.println("Error loading samples: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }
}