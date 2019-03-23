package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.service.CustomerService;
import de.alexanderhofmeister.rechnungen.util.ButtonUtil;
import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerOverviewController implements Initializable {

    @FXML
    @Getter
    private TableView<Customer> customerTable;

    private final static int ROWS_PER_PAGE = 10;

    final static private int CUSTOMER_SIZE = new CustomerService().countAll();

    @FXML
    private Button newCustomer;

    @FXML
    private Pagination pagination;

    private CustomerService customerService = new CustomerService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initTable();

        this.newCustomer.setOnAction(e -> loadCustomerEdit(new Customer()));

        pagination.setPageFactory(this::createPage);
        pagination.setPageCount((int) Math.ceil(CUSTOMER_SIZE / (ROWS_PER_PAGE)));
        pagination.setCurrentPageIndex(0);

    }

    private void initTable() {
        this.customerTable = new TableView<>();
        this.customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.customerTable.setPrefSize(600, 500);

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
        TableColumn<Customer, String> id = new TableColumn<>("#");
        id.setPrefWidth(50);

        TableColumn<Customer, String> name = new TableColumn<>("Kunde");
        name.setPrefWidth(150);

        TableColumn<Customer, String> address = new TableColumn<>("Anschrift");
        address.setPrefWidth(250);

        TableColumn<Customer, Customer> action = new TableColumn<>("Aktionen");
        action.setPrefWidth(150);

        this.customerTable.getColumns().add(id);
        this.customerTable.getColumns().add(name);
        this.customerTable.getColumns().add(address);
        this.customerTable.getColumns().add(action);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("company"));
        address.setCellValueFactory(tableCell -> new SimpleStringProperty(tableCell.getValue().getAddress()));

        action.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        action.setCellFactory(param -> new TableCell<Customer, Customer>() {

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

    private Node createPage(int pageIndex) {
        this.customerTable.setItems(FXCollections.observableArrayList(customerService.listAll(pageIndex * ROWS_PER_PAGE, Math.min(ROWS_PER_PAGE, CUSTOMER_SIZE))));
        return new BorderPane(this.customerTable);
    }
}