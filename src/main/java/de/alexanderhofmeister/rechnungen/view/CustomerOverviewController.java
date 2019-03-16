package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.service.CustomerService;
import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class CustomerOverviewController implements Initializable {

    @FXML
    @Getter
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> name;

    @FXML
    private TableColumn<Customer, String> id;

    @FXML
    private TableColumn<Customer, String> address;

    @FXML
    private TableColumn<Customer, Customer> action;

    @FXML
    private TextField filterField;

    @FXML
    private Button newCustomer;

    private CustomerService customerService = new CustomerService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
    }

    private void initTable() {
        final ObservableList<Customer> allCustomer = FXCollections.observableArrayList(this.customerService.listAll());

        final FilteredList<Customer> filteredData = new FilteredList<>(allCustomer);

        this.filterField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(myObject -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            return Stream.of(myObject.getCompany(), myObject.getCompanyAddition(), String.valueOf(myObject.getId()))
                    .map(String::toLowerCase).anyMatch((filterValue -> filterValue.contains(newValue.toLowerCase())));

        }));

        final SortedList<Customer> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(this.customerTable.comparatorProperty());

        this.customerTable.setItems(sortedData);
        this.id.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.name.setCellValueFactory(new PropertyValueFactory<>("company"));
        this.address.setCellValueFactory(tableCell -> new SimpleStringProperty(tableCell.getValue().getAddress()));
        this.newCustomer.setOnAction(e -> loadCustomerEdit(new Customer()));


        customerTable.setRowFactory(tv -> {
            final TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadCustomerEdit(row.getItem());
                }
            });
            return row;
        });

        this.action.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        this.action.setCellFactory(param -> new TableCell<Customer, Customer>() {

            @Override
            protected void updateItem(final Customer entity, final boolean empty) {
                super.updateItem(entity, empty);

                if (entity == null) {
                    setGraphic(null);
                    return;
                }
                final Button editButton = new Button();
                final FontAwesomeIcon editIcon = new FontAwesomeIcon();
                editIcon.setIcon(FontAwesomeIconName.EDIT);
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("button");
                editButton.setOnAction(event -> loadCustomerEdit(entity));

                final Button deleteButton = new Button();
                final FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                deleteIcon.setIcon(FontAwesomeIconName.TRASH);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("button");
                deleteButton.setOnAction(event -> {
                    customerService.delete(entity);
                    initTable();
                });

                setGraphic(new HBox(15, editButton, deleteButton));

            }
        });
    }

    private void loadCustomerEdit(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();

        dialog.setHeaderText(customer.isNew() ? "Neuer Kunde" : "Kunde " + customer.getCompany() + " bearbeiten");

        Pair<Pane, Object> eventNew = FxmlUtil.loadFxml(this, "editCustomer");
        CustomerEditController controller = (CustomerEditController) eventNew.getValue();
        controller.setCustomer(customer);


        ButtonType createType = new ButtonType(customer.isNew() ? "Erstellen" : "Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(eventNew.getKey());


        dialog.show();
        final Button createCustomerType = (Button) dialog.getDialogPane().lookupButton(createType);
        createCustomerType.addEventFilter(ActionEvent.ACTION, ae -> {

            try {
                customer.setCompany(controller.getCompany());
                customer.setCompanyAddition(controller.getCompanyAddition());
                customer.setStreet(controller.getStreet());
                customer.setStreetNumber(controller.getStreetNumber());
                customer.setZipCode(controller.getZipCode());
                customer.setCity(controller.getCity());
                customer.setEmail(controller.getEmail());
                customer.setContactPerson(controller.getContactPerson());
                this.customerService.update(customer);
            } catch (BusinessException e) {
                ae.consume();
                controller.setErrorText(e.getMessage());
            }

        });

    }
}