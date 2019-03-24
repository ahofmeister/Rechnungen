package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.BaseEntity;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.service.AbstractEntityService;
import de.alexanderhofmeister.rechnungen.service.ClassUtil;
import de.alexanderhofmeister.rechnungen.service.QueryParameter;
import de.alexanderhofmeister.rechnungen.util.ButtonUtil;
import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class EntityOverviewController<E extends BaseEntity, C extends EntityEditController> implements Initializable {


    private final static int ROWS_PER_PAGE = 10;
    @FXML
    protected TableView<E> entityTable;
    @FXML
    protected HBox pageContainer;
    @FXML
    private Label hitCount;
    @FXML
    private TextField filter;
    @FXML
    private Button newEntity;


    protected abstract String getFilterNamedQuery();

    protected abstract String getFilterCountNamedQuery();

    protected abstract AbstractEntityService<E> getService();

    protected abstract List<TableColumn<E, ?>> getEntityColumns();


    protected abstract String getEditViewFileName();

    protected abstract void mapEditEntity(E entity, C controller);


    private void initData() {
        Map<String, Object> parameters = QueryParameter.with("filter", "%" + this.filter.getText() + "%").parameters();
        Long foundEntitySize = getService().findCountWithNamedQuery(getFilterCountNamedQuery(), parameters);

        ObservableList<E> foundCustomer = FXCollections.observableArrayList(getService().findWithNamedQuery(
                getFilterNamedQuery(), parameters, 0, Math.min(ROWS_PER_PAGE, Math.toIntExact(foundEntitySize))));
        this.entityTable.setItems(foundCustomer);
        this.hitCount.setText(String.format("%s Treffer", foundEntitySize));
        this.entityTable.visibleProperty().setValue(foundEntitySize > 0);

        this.pageContainer.getChildren().clear();

        int maxRow = Math.toIntExact(Math.min(ROWS_PER_PAGE, foundEntitySize));
        for (int i = 0; i < (Math.ceil(foundEntitySize * 1.0 / ROWS_PER_PAGE)); i++) {
            Button pageButton = new Button(String.valueOf(i + 1));
            int finalI = i;
            pageButton.setOnAction(e -> this.entityTable.setItems(FXCollections.observableArrayList(getService().findWithNamedQuery
                    (getFilterNamedQuery(), parameters, finalI * ROWS_PER_PAGE, maxRow))));
            this.pageContainer.getChildren().add(pageButton);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        initTable();
        this.filter.textProperty().addListener(listener -> initData());
        this.newEntity.setOnAction(e -> {
            try {
                loadEntityEdit((E) ClassUtil.getActualTypeBinding(getClass(), EntityOverviewController.class, 0).newInstance());
            } catch (InstantiationException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        });

    }


    private void initTable() {
        this.entityTable.getColumns().clear();

        TableColumn<E, String> id = new TableColumn<>("#");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<E, E> actions = new TableColumn<>("Aktionen");
        actions.setPrefWidth(300);
        actions.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actions.setCellFactory(param -> new TableCell<E, E>() {

            @Override
            protected void updateItem(final E entity, final boolean empty) {
                super.updateItem(entity, empty);

                if (entity == null) {
                    setGraphic(null);
                    return;
                }
                final Button editButton = ButtonUtil.createEditButton(event -> loadEntityEdit(entity));
                final Button deleteButton = ButtonUtil.createDeleteButton(event -> {
                    getService().delete(entity);
                    initData();
                    initTable();
                });

                HBox value = new HBox(10, editButton, deleteButton);
                List<Button> customButtons = getCustomButtons(entity);
                if (customButtons != null) {
                    value.getChildren().addAll(customButtons);
                }
                setGraphic(value);

            }


        });
        this.entityTable.getColumns().add(id);
        this.entityTable.getColumns().addAll(getEntityColumns());
        this.entityTable.getColumns().add(actions);


        this.entityTable.setRowFactory(tv -> {
            final TableRow<E> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadEntityEdit(row.getItem());
                }
            });
            return row;
        });
    }

    abstract List<Button> getCustomButtons(E entity);


    private void loadEntityEdit(E entity) {
        Dialog<E> dialog = new Dialog<>();

        String title = entity.getTitle();
        dialog.setHeaderText(entity.isNew() ? title + " anlegen" : title + " " + entity.toString() + " bearbeiten");

        Pair<Pane, Object> eventNew = FxmlUtil.loadFxml(this, getEditViewFileName());
        C controller = (C) eventNew.getValue();
        controller.mapEntity(entity);


        ButtonType createType = new ButtonType(entity.isNew() ? "Erstellen" : "Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(eventNew.getKey());

        dialog.show();
        final Button createCustomerType = (Button) dialog.getDialogPane().lookupButton(createType);
        createCustomerType.addEventFilter(ActionEvent.ACTION, ae -> {

            try {
                mapEditEntity(entity, controller);
                getService().update(entity);
                initData();
                initTable();
            } catch (BusinessException e) {
                ae.consume();
                controller.setErrorText(e.getMessage());
            }

        });

    }
}