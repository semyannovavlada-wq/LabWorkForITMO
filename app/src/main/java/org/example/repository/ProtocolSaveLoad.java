package org.example.repository;

import org.example.domain.MeasurementParam;
import org.example.domain.Protocol;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class ProtocolSaveLoad implements SaveLoad<Protocol, Long> {
    private final File file;

    public ProtocolSaveLoad(String dataDir) {
        this.file = new File(dataDir, "protocols.json");
        ensureDirectoryExists(dataDir);
    }

    private void ensureDirectoryExists(String dataDir) {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String toJson(Protocol p) {
        StringBuilder paramsBuilder = new StringBuilder();
        List<MeasurementParam> list = new ArrayList<>(p.getRequiredParams());
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) paramsBuilder.append(",");
            paramsBuilder.append(list.get(i).name());
        }

        return "{" +
                "\"id\": " + p.getId() + "," +
                "\"name\": \"" + JsonUtils.escape(p.getName()) + "\"," +
                "\"requiredParams\": \"" + paramsBuilder.toString() + "\"," +
                "\"owner\": \"" + JsonUtils.escape(p.getOwnerUsername()) + "\"," +
                "\"createdAt\": \"" + p.getCreatedAt().toString() + "\"," +
                "\"updatedAt\": \"" + p.getUpdatedAt().toString() + "\"" +
                "}";
    }

    private Protocol fromJson(String json) {
        try {
            long id = JsonUtils.extractLong(json, "id");
            String name = JsonUtils.extractString(json, "name");
            Set<MeasurementParam> requiredParams = new HashSet<>();
            String paramsStr = JsonUtils.extractString(json, "requiredParams");
            if (!paramsStr.isEmpty()) {
                for (String p : paramsStr.split(",")) {
                    requiredParams.add(MeasurementParam.valueOf(p.trim()));
                }
            }
            String owner = JsonUtils.extractString(json, "owner");
            Instant createdAt = Instant.parse(JsonUtils.extractString(json, "createdAt"));
            Instant updatedAt = Instant.parse(JsonUtils.extractString(json, "updatedAt"));

            return new Protocol(id, name, requiredParams, owner, createdAt, updatedAt);
        } catch (Exception e) {
            System.err.println("Error parsing Protocol JSON: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Map<Long, Protocol> protocols) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("[");
            List<Protocol> list = new ArrayList<>(protocols.values());
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
            System.err.println("Error saving protocols: " + e.getMessage());
        }
    }

    @Override
    public Map<Long, Protocol> load() {
        Map<Long, Protocol> result = new HashMap<>();
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

                String protocolJson = json.substring(start, end + 1);
                Protocol protocol = fromJson(protocolJson);
                if (protocol != null) {
                    result.put(protocol.getId(), protocol);
                }
                start = json.indexOf("{", end + 1);
            }
        } catch (IOException e) {
            System.err.println("Error loading protocols: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }
}