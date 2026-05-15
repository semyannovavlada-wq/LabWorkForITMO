package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.domain.MeasurementParam;
import java.util.HashSet;
import java.util.Set;

public class ProtocolDialog extends Dialog<Object[]> {

    private final TextField nameField = new TextField();
    private final CheckBox phBox = new CheckBox("pH");
    private final CheckBox condBox = new CheckBox("Conductivity");
    private final CheckBox turbBox = new CheckBox("Turbidity");
    private final CheckBox nitBox = new CheckBox("Nitrate");

    public ProtocolDialog(String title) {
        setTitle(title);

        ButtonType okButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        VBox paramBox = new VBox(5, phBox, condBox, turbBox, nitBox);
        paramBox.setPadding(new Insets(5));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Required params:"), 0, 1);
        grid.add(paramBox, 1, 1);

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button == okButton) {
                String name = nameField.getText().trim();
                Set<MeasurementParam> params = new HashSet<>();
                if (phBox.isSelected()) params.add(MeasurementParam.PH);
                if (condBox.isSelected()) params.add(MeasurementParam.CONDUCTIVITY);
                if (turbBox.isSelected()) params.add(MeasurementParam.TURBIDITY);
                if (nitBox.isSelected()) params.add(MeasurementParam.NITRATE);

                if (!name.isEmpty() && !params.isEmpty()) {
                    return new Object[]{name, params};
                }
            }
            return null;
        });
    }
}