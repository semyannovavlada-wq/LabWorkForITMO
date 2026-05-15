package org.example.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.services.MeasurementService;
import org.example.services.ProtocolService;
import org.example.services.SampleService;
import org.example.storage.StorageService;

public class AppFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SampleService sampleService = new SampleService();
        MeasurementService measurementService = new MeasurementService(sampleService);
        ProtocolService protocolService = new ProtocolService(sampleService, measurementService);

        StorageService storageService = new StorageService(sampleService, measurementService, protocolService);
        storageService.loadAll();

        stage.setOnCloseRequest(e -> storageService.saveAll());

        SampleController controller = new SampleController(sampleService, measurementService, protocolService);
        MainView mainView = new MainView(controller);

        Scene scene = new Scene(mainView, 1000, 650);
        stage.setTitle("WaterLab - Sample Manager");
        stage.setScene(scene);
        stage.show();
    }
}