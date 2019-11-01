package de.alexanderhofmeister.rechnungen.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class ButtonUtil {

    private static Button createButton(EventHandler<ActionEvent> action) {
        final Button button = new Button();
        button.getStyleClass().add("button");
        button.setOnAction(action);
        return button;
    }

    private static Button createButton(EventHandler<ActionEvent> action, String tooltipText) {
        Button button = createButton(action);
        Tooltip tooltip = new Tooltip(tooltipText);
        button.setTooltip(tooltip);
        return button;
    }

    public static Button createEditButton(EventHandler<ActionEvent> action) {
        return createIconButton(action, FontAwesomeIconName.EDIT, "Bearbeiten");
    }

    public static Button createDeleteButton(EventHandler<ActionEvent> action) {
        return createIconButton(action, FontAwesomeIconName.TRASH, "Löschen");
    }

    public static Button createIconButton(EventHandler<ActionEvent> action, FontAwesomeIconName icontype, String tooltip) {
        Button button = createButton(action, tooltip);
        final FontAwesomeIcon icon = new FontAwesomeIcon();
        icon.setIcon(icontype);
        button.setGraphic(icon);
        return button;
    }
}
