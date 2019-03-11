package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class MainController {

    @FXML
    private Pane mainPane;

    public void switchToCustomerPane() {
        Pair<Pane, Object> customerOverview = FxmlUtil.loadFxml(this, "customerOverview");
        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add(customerOverview.getKey());
    }

    public void exitApplication() {
        Platform.exit();
    }

}