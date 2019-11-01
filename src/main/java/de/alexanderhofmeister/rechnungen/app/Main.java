package de.alexanderhofmeister.rechnungen.app;

import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        final Scene scene = new Scene(FxmlUtil.loadFxml(this, "main").getKey());

        new Thread(BillService::new).start();

        scene.getStylesheets().add("/css/style.css");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
