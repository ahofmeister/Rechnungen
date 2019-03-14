package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Properties;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewPropertiesController implements Initializable {

    @FXML
    private VBox propertiesContainer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createInputFields();
        createButtons();
    }

    private void createButtons() {
        final Properties properties = Properties.getInstance();
        HBox buttons = new HBox(20);
        Button save = new Button("Speichern");
        Button reload = new Button("Zurücksetzen");
        save.setOnAction(e -> {
            try {
                properties.getKeys().forEach(key -> properties.setProperty(key, findTextField(key).getText()));
                properties.save();
            } catch (ConfigurationException e1) {
                // TODO ERROR HANDLING
                e1.printStackTrace();
            }
        });
        reload.setOnAction(event -> properties.getKeys().forEach(key -> findTextField(key).setText(properties.getString(key))));
        buttons.getChildren().addAll(save, reload);
        this.propertiesContainer.getChildren().add(buttons);
    }

    private TextField findTextField(String key) {
        return (TextField) this.propertiesContainer.lookup("#" + key);
    }

    private void createInputFields() {
        Properties instance = Properties.getInstance();
        instance.getKeys().forEach(key -> {
            HBox hBox = new HBox(20);
            TextField valueInput = new TextField(instance.getString(key));
            valueInput.setId(key);
            hBox.getChildren().addAll(new Label(key), valueInput);
            this.propertiesContainer.getChildren().add(hBox);
        });

    }
}