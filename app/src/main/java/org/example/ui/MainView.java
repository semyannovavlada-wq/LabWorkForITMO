package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.example.domain.*;

import java.util.Set;

public class MainView extends BorderPane {

    private final SampleController controller;
    private final TableView<Sample> sampleTable = new TableView<>();
    private final TableView<Measurement> measurementTable = new TableView<>();
    private final TableView<Protocol> protocolTable = new TableView<>();
    private final Label statusLabel = new Label("Ready");
    private final ProgressBar progressBar = new ProgressBar(0);
    private final Label selectedInfo = new Label();

    public MainView(SampleController controller) {
        this.controller = controller;
        buildUI();
        controller.refresh();
        controller.loadProtocols();
    }

    private void buildUI() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(8));
        topBar.setStyle("-fx-background-color: #6FAEDB;");

        Label title = new Label("WaterLab - Sample Manager");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> controller.refresh());

        Button addSampleBtn = new Button("Add Sample");
        addSampleBtn.setOnAction(e -> showAddSampleDialog());

        Button editSampleBtn = new Button("Edit Sample");
        editSampleBtn.setOnAction(e -> showEditSampleDialog());

        Button deleteSampleBtn = new Button("Delete Sample");
        deleteSampleBtn.setOnAction(e -> showDeleteSampleDialog());

        Button addMeasBtn = new Button("Add Measurement");
        addMeasBtn.setOnAction(e -> showAddMeasurementDialog());

        Button addProtBtn = new Button("Add Protocol");
        addProtBtn.setOnAction(e -> showAddProtocolDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(title, spacer, refreshBtn, addSampleBtn, editSampleBtn,
                deleteSampleBtn, addMeasBtn, addProtBtn);
        setTop(topBar);

        SplitPane mainSplit = new SplitPane();
        mainSplit.setDividerPositions(0.35, 0.70);

        setupSampleTable();
        VBox leftBox = new VBox(5, new Label("Samples"), sampleTable);
        leftBox.setPadding(new Insets(5));

        setupMeasurementTable();
        setupProtocolTable();
        selectedInfo.setPadding(new Insets(5));
        selectedInfo.setStyle("-fx-background-color: #E4E8EE;");

        VBox middleBox = new VBox(5, selectedInfo, new Label("Measurements"), measurementTable);
        middleBox.setPadding(new Insets(5));

        VBox rightBox = new VBox(5, new Label("Protocols"), protocolTable);
        rightBox.setPadding(new Insets(5));

        mainSplit.getItems().addAll(leftBox, middleBox, rightBox);
        setCenter(mainSplit);

        VBox bottomBox = new VBox(3, progressBar, statusLabel);
        bottomBox.setPadding(new Insets(5));
        setBottom(bottomBox);

        sampleTable.setItems(controller.getSamples());
        measurementTable.setItems(controller.getMeasurements());
        protocolTable.setItems(controller.getProtocols());

        sampleTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            controller.select(val);
            if (val != null) {
                selectedInfo.setText("Selected: " + val.getName() + " | Type: " + val.getType()
                        + " | Location: " + val.getLocation() + " | Status: " + val.getStatus());
            } else {
                selectedInfo.setText("");
            }
        });

        controller.getSamples().addListener((javafx.collections.ListChangeListener<Sample>) c ->
                statusLabel.setText(controller.getStatus()));
    }

    private void setupSampleTable() {
        TableColumn<Sample, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Sample, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(100);

        TableColumn<Sample, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(80);

        TableColumn<Sample, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locCol.setPrefWidth(80);

        TableColumn<Sample, SampleStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        sampleTable.getColumns().addAll(idCol, nameCol, typeCol, locCol, statusCol);
    }

    private void setupMeasurementTable() {
        TableColumn<Measurement, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Measurement, String> paramCol = new TableColumn<>("Param");
        paramCol.setCellValueFactory(new PropertyValueFactory<>("param"));
        paramCol.setPrefWidth(90);

        TableColumn<Measurement, Double> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valCol.setPrefWidth(60);

        TableColumn<Measurement, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitCol.setPrefWidth(60);

        TableColumn<Measurement, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("method"));
        methodCol.setPrefWidth(100);

        measurementTable.getColumns().addAll(idCol, paramCol, valCol, unitCol, methodCol);
    }

    private void setupProtocolTable() {
        TableColumn<Protocol, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Protocol, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(120);

        TableColumn<Protocol, String> paramsCol = new TableColumn<>("Required Params");
        paramsCol.setCellValueFactory(new PropertyValueFactory<>("requiredParams"));
        paramsCol.setPrefWidth(180);

        protocolTable.getColumns().addAll(idCol, nameCol, paramsCol);
    }

    private void showAddSampleDialog() {
        SampleDialog dialog = new SampleDialog("Add Sample");
        dialog.showAndWait().ifPresent(result ->
                controller.addSample(result[0], result[1], result[2]));
    }

    private void showEditSampleDialog() {
        Sample selected = controller.getSelectedSample();
        if (selected == null) {
            showError("Select a sample first");
            return;
        }
        SampleDialog dialog = new SampleDialog("Edit Sample", selected.getName(),
                selected.getType(), selected.getLocation());
        dialog.showAndWait().ifPresent(result ->
                controller.editSample(selected.getId(), result[0], result[2]));
    }

    private void showDeleteSampleDialog() {
        Sample selected = controller.getSelectedSample();
        if (selected == null) {
            showError("Select a sample first");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Sample");
        confirm.setHeaderText("Delete " + selected.getName() + "?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) controller.deleteSample(selected.getId());
        });
    }

    private void showAddMeasurementDialog() {
        Sample selected = controller.getSelectedSample();
        if (selected == null) {
            showError("Select a sample first");
            return;
        }
        MeasurementDialog dialog = new MeasurementDialog("Add Measurement");
        dialog.showAndWait().ifPresent(result ->
                controller.addMeasurement(selected.getId(), (MeasurementParam) result[0],
                        (Double) result[1], (String) result[2], (String) result[3]));
    }

    private void showAddProtocolDialog() {
        ProtocolDialog dialog = new ProtocolDialog("Create Protocol");
        dialog.showAndWait().ifPresent(result ->
                controller.addProtocol((String) result[0], (Set<MeasurementParam>) result[1]));
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}