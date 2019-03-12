package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class MainController {

    @FXML
    private Pane mainPane;

    public void switchToCustomerPane() {
        switchPane("customerOverview");
    }

    private void switchPane(String filename) {
        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add(FxmlUtil.loadFxml(this, filename).getKey());
    }

    public void exitApplication() {
        Platform.exit();
    }

    public void switchToBillPane() {
        switchPane("billOverview");
    }
}