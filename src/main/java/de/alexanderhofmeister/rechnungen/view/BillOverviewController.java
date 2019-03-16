package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Properties;
import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.util.*;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class BillOverviewController implements Initializable {

    @FXML
    @Getter
    private TableView<Bill> billTable;

    @FXML
    private TableColumn<Bill, String> number;

    @FXML
    private TableColumn<Bill, String> customer;

    @FXML
    private TableColumn<Bill, String> total;

    @FXML
    private TableColumn<Bill, String> date;

    @FXML
    private TableColumn<Bill, Bill> action;

    @FXML
    private TextField filterField;

    @FXML
    private Button newBill;

    private BillService billService = new BillService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
    }

    private void initTable() {
        final ObservableList<Bill> allBills = FXCollections.observableArrayList(this.billService.listAll());

        final FilteredList<Bill> filteredData = new FilteredList<>(allBills);

        this.filterField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(myObject -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }


            return Stream.of(myObject.getCustomer().getCompany(), myObject.getCustomer().getCompanyAddition(), String.valueOf(myObject.getNumber()))
                    .map(String::toLowerCase).anyMatch((filterValue -> filterValue.contains(newValue.toLowerCase())));
        }));

        final SortedList<Bill> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(this.billTable.comparatorProperty());

        this.billTable.setItems(sortedData);
        this.number.setCellValueFactory(new PropertyValueFactory<>("number"));
        this.customer.setCellValueFactory(new PropertyValueFactory<>("customer"));

        this.date.setCellValueFactory(tableCell -> new SimpleStringProperty(DateUtil.formatToDisplayDate(tableCell.getValue().getDate())));
        this.total.setCellValueFactory(new PropertyValueFactory<>("total"));
        this.newBill.setOnAction(e -> loadBillEdit(new Bill()));

        billTable.setRowFactory(tv -> {
            final TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadBillEdit(row.getItem());
                }
            });
            return row;
        });


        this.action.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        this.action.setCellFactory(param -> new TableCell<Bill, Bill>() {

            @Override
            protected void updateItem(final Bill bill, final boolean empty) {
                super.updateItem(bill, empty);

                if (bill == null) {
                    setGraphic(null);
                    return;
                }
                final Button editButton = ButtonUtil.createEditButton(event -> loadBillEdit(bill));
                final Button deleteButton = ButtonUtil.createDeleteButton(event -> {
                    billService.delete(bill);
                    initTable();
                });

                final Button exportAsPdf = ButtonUtil.createIconButton(event -> exportBill(bill), FontAwesomeIconName.
                        FILE, "Speichern");
                final Button printButton = ButtonUtil.createIconButton(event1 -> ExportUtil.printFile(exportBill(bill)), FontAwesomeIconName.PRINT, "Drucken");


                final Button emailButton = ButtonUtil.createIconButton(event -> {

                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("bill", bill);
                    try {
                        String body = ExportUtil.fillTemplateFromVariables("emailBill", attributes);
                        ExportUtil.sendViaEmail(exportBill(bill), bill.toString(), body, bill.getCustomer().getEmail());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }, FontAwesomeIconName.SEND, "Als E-Mail senden");


                setGraphic(new HBox(10, editButton, deleteButton, exportAsPdf, printButton, emailButton));

            }
        });
    }

    private File exportBill(Bill entity) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("customer", entity.getCustomer());
        attributes.put("bill", entity);
        attributes.put("MoneyUtil", MoneyUtil.class);
        attributes.put("DateUtil", DateUtil.class);
        attributes.put("Properties", Properties.getInstance());
        return ExportUtil.createFileFromTemplate(entity.getDate(), new File(ExportUtil.getFileNameBill(entity)), "bill", attributes);
    }


    private void loadBillEdit(Bill bill) {

        Dialog<Bill> dialog = new Dialog<>();

        dialog.setHeaderText(bill.isNew() ? "Neuer Rechnung" : "Rechnung " + bill.getNumber() +
                " für " + bill.getCustomer().getCompany() + " bearbeiten");

        Pair<Pane, Object> eventNew = FxmlUtil.loadFxml(this, "editBill");
        BillEditController controller = (BillEditController) eventNew.getValue();
        controller.setBill(bill);


        ButtonType createType = new ButtonType(bill.isNew() ? "Erstellen" : "Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(eventNew.getKey());


        dialog.show();
        final Button createBillType = (Button) dialog.getDialogPane().lookupButton(createType);
        createBillType.addEventFilter(ActionEvent.ACTION, ae -> {

            try {
                bill.setNumber(controller.getNumber());
                bill.setDate(controller.getDate());
                bill.setCustomer(controller.getCustomer());
                bill.setAmount(controller.getAmount());
                bill.setVat(controller.getVat());
                bill.setSubtotal(controller.getSubTotal());
                bill.setPostage(controller.getPostage());
                bill.setTotal(controller.getTotal());
                bill.setEntries(controller.getBillEntries());
                this.billService.update(bill);
                initTable();
            } catch (BusinessException e) {
                ae.consume();
                controller.setErrorText(e.getMessage());
            }

        });

    }
}
