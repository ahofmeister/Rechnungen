package de.alexanderhofmeister.rechnungen.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ViewPropertiesController implements Initializable {

    @FXML
    private VBox propertiesContainer;

    private PropertiesConfiguration propertiesConfiguration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            propertiesConfiguration = new PropertiesConfiguration("./config.properties");
        } catch (ConfigurationException e) {
            // TODO ERROR HANDLING
            e.printStackTrace();
            return;
        }
        createInputFields();
        createButtons();
    }

    private void createButtons() {
        HBox buttons = new HBox(20);
        Button save = new Button("Speichern");
        Button reload = new Button("Zurücksetzen");
        save.setOnAction(e -> {
            try {
                Iterator<String> keys = propertiesConfiguration.getKeys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    this.propertiesConfiguration.setProperty(key, findTextField(key).getText());
                }
                this.propertiesConfiguration.save();
            } catch (ConfigurationException e1) {
                // TODO ERROR HANDLING
                e1.printStackTrace();
            }
        });
        reload.setOnAction(event -> {
            Iterator<String> keys = propertiesConfiguration.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                findTextField(key).setText(propertiesConfiguration.getString(key));
            }

        });
        buttons.getChildren().addAll(save, reload);
        this.propertiesContainer.getChildren().add(buttons);
    }

    private TextField findTextField(String key) {
        return (TextField) this.propertiesContainer.lookup("#" + key);
    }

    private void createInputFields() {
        Iterator<String> keys = this.propertiesConfiguration.getKeys();
        while (keys.hasNext()) {
            HBox hBox = new HBox(20);
            String key = keys.next();
            TextField valueInput = new TextField(this.propertiesConfiguration.getString(key));
            valueInput.setId(key);
            hBox.getChildren().addAll(new Label(key), valueInput);
            this.propertiesContainer.getChildren().add(hBox);
        }
    }
}