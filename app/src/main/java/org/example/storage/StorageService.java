package org.example.storage;

import com.google.gson.reflect.TypeToken;
import org.example.domain.Measurement;
import org.example.domain.Protocol;
import org.example.domain.Sample;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.saveLoad.JsonSaveLoad;
import org.example.storage.validation.FileValidator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class StorageService {

    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;

    private final JsonSaveLoad<Sample, Long> sampleStorage;
    private final JsonSaveLoad<Measurement, Long> measurementStorage;
    private final JsonSaveLoad<Protocol, Long> protocolStorage;

    public StorageService(SampleService sampleService,
                          MeasurementService measurementService,
                          ProtocolService protocolService) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;

        this.sampleStorage = new JsonSaveLoad<>(
                AppData.resolve("samples.json"),
                Sample::getId,
                new TypeToken<List<Sample>>() {}.getType()
        );
        this.measurementStorage = new JsonSaveLoad<>(
                AppData.resolve("measurements.json"),
                Measurement::getId,
                new TypeToken<List<Measurement>>() {}.getType()
        );
        this.protocolStorage = new JsonSaveLoad<>(
                AppData.resolve("protocols.json"),
                Protocol::getId,
                new TypeToken<List<Protocol>>() {}.getType()
        );
    }

    // Сохранение в папку по умолчанию
    public void saveAll() {
        sampleStorage.save(sampleService.getAll());
        measurementStorage.save(measurementService.getAll());
        protocolStorage.save(protocolService.getAll());
        System.out.println("Data saved.");
    }

    public void loadAll() {
        loadAllInternal(sampleStorage, measurementStorage, protocolStorage);
    }

    public void saveAll(String directory) {
        Path dir = Path.of(directory);
        new JsonSaveLoad<>(
                dir.resolve("samples.json"),
                Sample::getId,
                new TypeToken<List<Sample>>() {}.getType()
        ).save(sampleService.getAll());

        new JsonSaveLoad<>(
                dir.resolve("measurements.json"),
                Measurement::getId,
                new TypeToken<List<Measurement>>() {}.getType()
        ).save(measurementService.getAll());

        new JsonSaveLoad<>(
                dir.resolve("protocols.json"),
                Protocol::getId,
                new TypeToken<List<Protocol>>() {}.getType()
        ).save(protocolService.getAll());

        System.out.println("Saved to " + directory);
    }

    public void loadAll(String directory) {
        Path dir = Path.of(directory);

        JsonSaveLoad<Sample, Long> sampleSl = new JsonSaveLoad<>(
                dir.resolve("samples.json"),
                Sample::getId,
                new TypeToken<List<Sample>>() {}.getType()
        );
        JsonSaveLoad<Measurement, Long> measurementSl = new JsonSaveLoad<>(
                dir.resolve("measurements.json"),
                Measurement::getId,
                new TypeToken<List<Measurement>>() {}.getType()
        );
        JsonSaveLoad<Protocol, Long> protocolSl = new JsonSaveLoad<>(
                dir.resolve("protocols.json"),
                Protocol::getId,
                new TypeToken<List<Protocol>>() {}.getType()
        );

        loadAllInternal(sampleSl, measurementSl, protocolSl);
    }

    private void loadAllInternal(JsonSaveLoad<Sample, Long> sampleSl,
                                 JsonSaveLoad<Measurement, Long> measurementSl,
                                 JsonSaveLoad<Protocol, Long> protocolSl) {
        Map<Long, Sample> sampleMap = sampleSl.load();
        Map<Long, Measurement> measurementMap = measurementSl.load();
        Map<Long, Protocol> protocolMap = protocolSl.load();

        List<Sample> samples = List.copyOf(sampleMap.values());
        List<Measurement> measurements = List.copyOf(measurementMap.values());
        List<Protocol> protocols = List.copyOf(protocolMap.values());

        List<String> errors = FileValidator.validate(samples, measurements, protocols);
        if (!errors.isEmpty()) {
            System.err.println("Ошибка загрузки:");
            for (String e : errors) {
                System.err.println("  - " + e);
            }
            System.err.println("Данные не загружены. Предыдущие данные сохранены.");
            return;
        }

        sampleService.clear();
        for (Sample s : samples) {
            sampleService.addExisting(s);
        }

        measurementService.clear();
        for (Measurement m : measurements) {
            measurementService.addExisting(m);
        }

        protocolService.clear();
        for (Protocol p : protocols) {
            protocolService.addExisting(p);
        }

        System.out.println("Loaded: " + samples.size() + " samples, "
                + measurements.size() + " measurements, "
                + protocols.size() + " protocols.");
    }
}