package org.example.repository;

import org.example.domain.Measurement;
import org.example.domain.MeasurementParam;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class MeasurementSaveLoad implements SaveLoad<Measurement, Long> {
    private final File file;

    public MeasurementSaveLoad(String dataDir) {
        this.file = new File(dataDir, "measurements.json");
        ensureDirectoryExists(dataDir);
    }

    private void ensureDirectoryExists(String dataDir) {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String toJson(Measurement m) {
        return "{" +
                "\"id\": " + m.getId() + "," +
                "\"sampleId\": " + m.getSampleId() + "," +
                "\"param\": \"" + m.getParam().name() + "\"," +
                "\"value\": " + m.getValue() + "," +
                "\"unit\": \"" + JsonUtils.escape(m.getUnit()) + "\"," +
                "\"method\": \"" + JsonUtils.escape(m.getMethod()) + "\"," +
                "\"measuredAt\": \"" + m.getMeasuredAt().toString() + "\"," +
                "\"owner\": \"" + JsonUtils.escape(m.getOwnerUsername()) + "\"," +
                "\"createdAt\": \"" + m.getCreatedAt().toString() + "\"," +
                "\"updatedAt\": \"" + m.getUpdatedAt().toString() + "\"" +
                "}";
    }

    private Measurement fromJson(String json) {
        try {
            long id = JsonUtils.extractLong(json, "id");
            long sampleId = JsonUtils.extractLong(json, "sampleId");
            MeasurementParam param = MeasurementParam.valueOf(JsonUtils.extractString(json, "param"));
            double value = JsonUtils.extractDouble(json, "value");
            String unit = JsonUtils.extractString(json, "unit");
            String method = JsonUtils.extractString(json, "method");
            Instant measuredAt = Instant.parse(JsonUtils.extractString(json, "measuredAt"));
            String owner = JsonUtils.extractString(json, "owner");
            Instant createdAt = Instant.parse(JsonUtils.extractString(json, "createdAt"));
            Instant updatedAt = Instant.parse(JsonUtils.extractString(json, "updatedAt"));

            return new Measurement(id, sampleId, param, value, unit, method,
                    measuredAt, owner, createdAt, updatedAt);
        } catch (Exception e) {
            System.err.println("Error parsing Measurement JSON: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Map<Long, Measurement> measurements) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("[");
            List<Measurement> list = new ArrayList<>(measurements.values());
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
            System.err.println("Error saving measurements: " + e.getMessage());
        }
    }

    @Override
    public Map<Long, Measurement> load() {
        Map<Long, Measurement> result = new HashMap<>();
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

                String measurementJson = json.substring(start, end + 1);
                Measurement measurement = fromJson(measurementJson);
                if (measurement != null) {
                    result.put(measurement.getId(), measurement);
                }
                start = json.indexOf("{", end + 1);
            }
        } catch (IOException e) {
            System.err.println("Error loading measurements: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }
}