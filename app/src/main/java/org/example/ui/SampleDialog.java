package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class SampleDialog extends Dialog<String[]> {

    private final TextField nameField = new TextField();
    private final TextField typeField = new TextField();
    private final TextField locationField = new TextField();

    public SampleDialog(String title) {
        this(title, "", "", "");
    }

    public SampleDialog(String title, String name, String type, String location) {
        setTitle(title);
        nameField.setText(name);
        typeField.setText(type);
        locationField.setText(location);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeField, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button == okButton) {
                String n = nameField.getText().trim();
                String t = typeField.getText().trim();
                String l = locationField.getText().trim();
                if (!n.isEmpty() && !t.isEmpty() && !l.isEmpty()) {
                    return new String[]{n, t, l};
                }
            }
            return null;
        });
    }
}