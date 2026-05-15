package org.example.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.domain.*;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import java.util.List;
import java.util.Set;

public class SampleController {

    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;

    private final ObservableList<Sample> samples = FXCollections.observableArrayList();
    private final ObservableList<Measurement> measurements = FXCollections.observableArrayList();
    private final ObservableList<Protocol> protocols = FXCollections.observableArrayList();

    private Sample selectedSample;
    private String status = "Ready";
    private String error;

    public SampleController(SampleService sampleService,
                            MeasurementService measurementService,
                            ProtocolService protocolService) {
        this.sampleService = sampleService;
        this.measurementService = measurementService;
        this.protocolService = protocolService;
    }

    public void refresh() {
        new Thread(() -> {
            try {
                List<Sample> all = sampleService.getAll();
                Platform.runLater(() -> {
                    samples.setAll(all);
                    if (selectedSample != null && !samples.contains(selectedSample)) {
                        selectedSample = null;
                        measurements.clear();
                    }
                    status = "Loaded " + samples.size() + " samples";
                });
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public void select(Sample sample) {
        this.selectedSample = sample;
        measurements.clear();
        if (sample != null) {
            new Thread(() -> {
                try {
                    List<Measurement> list = measurementService.listBySample(sample.getId());
                    Platform.runLater(() -> measurements.setAll(list));
                } catch (Exception e) {
                    Platform.runLater(() -> error = e.getMessage());
                }
            }).start();
        }
    }

    public void addSample(String name, String type, String location) {
        new Thread(() -> {
            try {
                sampleService.add(name, type, location, "SYSTEM");
                Platform.runLater(() -> { refresh(); status = "Sample added"; });
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public void editSample(long id, String name, String location) {
        new Thread(() -> {
            try {
                Sample s = sampleService.getById(id);
                if (name != null && !name.isBlank()) s.setName(name);
                if (location != null && !location.isBlank()) s.setLocation(location);
                s.setUpdatedAt(java.time.Instant.now());
                Platform.runLater(() -> { refresh(); status = "Sample updated"; });
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public void deleteSample(long id) {
        new Thread(() -> {
            try {
                sampleService.updateStatus(id, SampleStatus.ARCHIVED, "SYSTEM");
                sampleService.remove(id, "SYSTEM");
                Platform.runLater(() -> {
                    if (selectedSample != null && selectedSample.getId() == id) select(null);
                    refresh();
                    status = "Sample deleted";
                });
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public void addMeasurement(long sampleId, MeasurementParam param, double value,
                               String unit, String method) {
        new Thread(() -> {
            try {
                measurementService.add(sampleId, param, value, unit, method, "SYSTEM");
                Platform.runLater(() -> { select(selectedSample); status = "Measurement added"; });
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public void addProtocol(String name, Set<MeasurementParam> params) {
        new Thread(() -> {
            try {
                protocolService.add(name, params, "SYSTEM");
                Platform.runLater(() -> { loadProtocols(); status = "Protocol created"; });
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public void loadProtocols() {
        new Thread(() -> {
            try {
                List<Protocol> list = protocolService.getAll();
                Platform.runLater(() -> protocols.setAll(list));
            } catch (Exception e) {
                Platform.runLater(() -> error = e.getMessage());
            }
        }).start();
    }

    public ObservableList<Sample> getSamples() { return samples; }
    public ObservableList<Measurement> getMeasurements() { return measurements; }
    public ObservableList<Protocol> getProtocols() { return protocols; }
    public Sample getSelectedSample() { return selectedSample; }
    public String getStatus() { return status; }
    public String getError() { return error; }
    public void clearError() { error = null; }
}