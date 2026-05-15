package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.domain.MeasurementParam;

public class MeasurementDialog extends Dialog<Object[]> {

    private final ComboBox<MeasurementParam> paramBox = new ComboBox<>();
    private final TextField valueField = new TextField();
    private final TextField unitField = new TextField();
    private final TextField methodField = new TextField();

    public MeasurementDialog(String title) {
        setTitle(title);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        paramBox.getItems().addAll(MeasurementParam.values());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Param:"), 0, 0);
        grid.add(paramBox, 1, 0);
        grid.add(new Label("Value:"), 0, 1);
        grid.add(valueField, 1, 1);
        grid.add(new Label("Unit:"), 0, 2);
        grid.add(unitField, 1, 2);
        grid.add(new Label("Method:"), 0, 3);
        grid.add(methodField, 1, 3);

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button == okButton) {
                MeasurementParam param = paramBox.getValue();
                String v = valueField.getText().trim();
                String u = unitField.getText().trim();
                String m = methodField.getText().trim();
                if (param != null && !v.isEmpty() && !u.isEmpty() && !m.isEmpty()) {
                    try {
                        return new Object[]{param, Double.parseDouble(v), u, m};
                    } catch (NumberFormatException ignored) {}
                }
            }
            return null;
        });
    }
}