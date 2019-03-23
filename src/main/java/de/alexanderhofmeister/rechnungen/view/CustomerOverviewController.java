package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.service.CustomerService;
import de.alexanderhofmeister.rechnungen.service.QueryParameter;
import de.alexanderhofmeister.rechnungen.util.ButtonUtil;
import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
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
import lombok.Getter;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerOverviewController implements Initializable {

    @FXML
    @Getter
    private TableView<Customer> customerTable;

    private final static int ROWS_PER_PAGE = 10;

    @FXML
    private Button newCustomer;

    @FXML
    TableColumn<Customer, String> id;
    @FXML
    TableColumn<Customer, String> name;
    @FXML
    TableColumn<Customer, String> address;
    @FXML
    TableColumn<Customer, Customer> actions;

    private CustomerService customerService = new CustomerService();

    @FXML
    private Label hitCount;

    @FXML
    private TextField filter;

    @FXML
    private HBox pageContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initTable();
        initData();

        this.filter.textProperty().addListener(listener -> initData());
        this.newCustomer.setOnAction(e -> loadCustomerEdit(new Customer()));


    }

    private void initData() {
        Map<String, Object> parameters = QueryParameter.with("filter", "%" + this.filter.getText() + "%").parameters();
        Long foundCustomerSize = customerService.findCountWithNamedQuery(Customer.NQ_COUNT_FILTER, parameters);

        ObservableList<Customer> foundCustomer = FXCollections.observableArrayList(customerService.findWithNamedQuery(
                Customer.NQ_FILTER, parameters, 0, Math.min(ROWS_PER_PAGE, Math.toIntExact(foundCustomerSize))));
        this.customerTable.setItems(foundCustomer);
        this.hitCount.setText(String.format("%s Treffer", foundCustomerSize));
        this.customerTable.visibleProperty().setValue(foundCustomerSize > 0);

        this.pageContainer.getChildren().clear();

        int maxRow = Math.toIntExact(Math.min(ROWS_PER_PAGE, foundCustomerSize));
        for (int i = 0; i < (Math.ceil(foundCustomerSize * 1.0 / ROWS_PER_PAGE)); i++) {
            Button pageButton = new Button(String.valueOf(i + 1));
            int finalI = i;
            pageButton.setOnAction(e -> this.customerTable.setItems(FXCollections.observableArrayList(customerService.findWithNamedQuery
                    (Customer.NQ_FILTER, parameters, finalI * ROWS_PER_PAGE, maxRow))));
            this.pageContainer.getChildren().add(pageButton);
        }
    }

    private void initTable() {
        this.customerTable.setRowFactory(tv -> {
            final TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadCustomerEdit(row.getItem());
                }
            });
            return row;
        });
        createColumns();
    }

    private void createColumns() {

        this.id.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.name.setCellValueFactory(new PropertyValueFactory<>("company"));
        this.address.setCellValueFactory(tableCell -> new SimpleStringProperty(tableCell.getValue().getAddress()));

        this.actions.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        this.actions.setCellFactory(param -> new TableCell<Customer, Customer>() {

            @Override
            protected void updateItem(final Customer customer, final boolean empty) {
                super.updateItem(customer, empty);

                if (customer == null) {
                    setGraphic(null);
                    return;
                }
                final Button editButton = ButtonUtil.createEditButton(event -> loadCustomerEdit(customer));
                final Button deleteButton = ButtonUtil.createDeleteButton(event -> {
                    customerService.delete(customer);
                    initTable();
                });

                final Button createBillButton = ButtonUtil.createIconButton(event -> {
                    Bill bill = new Bill();
                    bill.setCustomer(customer);
                    BillOverviewController.createBillView(bill, this, new BillService(), () -> {
                    });
                }, FontAwesomeIconName.MONEY, "Rechnung erstellen");

                setGraphic(new HBox(10, editButton, deleteButton, createBillButton));

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
                initTable();
            } catch (BusinessException e) {
                ae.consume();
                controller.setErrorText(e.getMessage());
            }

        });

    }
}