package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.BaseEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public abstract class EntityEditController<E extends BaseEntity> {


    @FXML
    private Label errorLabel;

    protected abstract void mapEntity(E entity);

    void setErrorText(String errors) {
        this.errorLabel.setText(errors);
    }
}
