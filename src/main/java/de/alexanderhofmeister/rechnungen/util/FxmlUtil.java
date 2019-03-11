package de.alexanderhofmeister.rechnungen.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;

public class FxmlUtil {


    /**
     * Loads the root pane from a fxml file with the given file.
     *
     * @param caller   the caller to ensure, that the right classloader loads the file
     * @param filename the filename of the fxml file, must be in a fxml directory
     * @return the found root pane and the controller as pair or @{code null}, if an exception occured
     */
    public static Pair<Pane, Object> loadFxml(Object caller, String filename) {
        final FXMLLoader loader = new FXMLLoader();
        Class<?> callerClass = caller.getClass();
        ClassLoader classLoader = callerClass.getClassLoader();
        loader.setClassLoader(classLoader);
        loader.setLocation(classLoader.getResource("fxml/" + filename + ".fxml"));
        try {
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
