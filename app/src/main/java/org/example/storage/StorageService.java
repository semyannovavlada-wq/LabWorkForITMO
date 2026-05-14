package org.example.storage;

import com.google.gson.reflect.TypeToken;
import org.example.domain.Measurement;
import org.example.domain.Protocol;
import org.example.domain.Sample;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.saveLoad.JsonSaveLoad;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StorageService {

    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;

    private final JsonSaveLoad<Sample, Long> sampleSaveLoad;
    private final JsonSaveLoad<Measurement, Long> measurementSaveLoad;
    private final JsonSaveLoad<Protocol, Long> protocolSaveLoad;

    public StorageService(SampleService sampleService,
                          MeasurementService measurementService,
                          ProtocolService protocolService) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;

        this.sampleSaveLoad = new JsonSaveLoad<>(
                AppData.resolve("samples.json"),
                Sample::getId,
                new TypeToken<List<Sample>>() {}.getType()
        );
        this.measurementSaveLoad = new JsonSaveLoad<>(
                AppData.resolve("measurements.json"),
                Measurement::getId,
                new TypeToken<List<Measurement>>() {}.getType()
        );
        this.protocolSaveLoad = new JsonSaveLoad<>(
                AppData.resolve("protocols.json"),
                Protocol::getId,
                new TypeToken<List<Protocol>>() {}.getType()
        );
    }

    public void saveAll() {
        sampleSaveLoad.save(sampleService.getAll());
        measurementSaveLoad.save(measurementService.getAll());
        protocolSaveLoad.save(protocolService.getAll());
        System.out.println("Data saved to JSON files.");
    }

    public void loadAll() {
        Map<Long, Sample> samples = sampleSaveLoad.load();
        Map<Long, Measurement> measurements = measurementSaveLoad.load();
        Map<Long, Protocol> protocols = protocolSaveLoad.load();

        sampleService.clear();
        for (Sample s : samples.values()) {
            sampleService.addExisting(s);
        }

        measurementService.clear();
        for (Measurement m : measurements.values()) {
            measurementService.addExisting(m);
        }

        protocolService.clear();
        for (Protocol p : protocols.values()) {
            protocolService.addExisting(p);
        }

        System.out.println("Loaded: " + samples.size() + " samples, "
                + measurements.size() + " measurements, "
                + protocols.size() + " protocols.");
    }
}