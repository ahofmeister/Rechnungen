package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.BillEntry;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.service.CustomerService;
import de.alexanderhofmeister.rechnungen.util.DateUtil;
import de.alexanderhofmeister.rechnungen.util.MoneyUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BillEditController extends EntityEditController<Bill> implements Initializable {

    private CustomerService customerService = new CustomerService();

    @FXML
    private Label amountTotal;

    @FXML
    private Label vat;

    @FXML
    private TextField postage;

    @FXML
    private Label subTotal;

    @FXML
    private Label total;

    @FXML
    private Button addBillEntry;

    @FXML
    private TextField position;

    @FXML
    private DatePicker period;

    @FXML
    private TextField amount;

    @FXML
    private TableView<BillEntry> billEntries;

    @FXML
    private TableColumn<BillEntry, String> positionColumn;

    @FXML
    private TableColumn<BillEntry, Integer> amountColumn;

    @FXML
    private TableColumn<BillEntry, String> periodColumn;

    @FXML
    private TableColumn<BillEntry, BillEntry> billEntryActionColumn;

    @FXML
    private TextField number;

    @FXML
    private TextField customer;

    @FXML
    private DatePicker date;

    @FXML
    private Label errorLabel;


    @Override
    protected void mapEntity(Bill bill) {
        Customer customer = bill.getCustomer();
        if (customer != null) {
            this.customer.setText(customer.getCompany() + " - " + customer.getCompanyAddition());
        }
        this.date.setValue(bill.getDate());
        this.billEntries.setItems(FXCollections.observableArrayList(bill.getEntries()));

        if (bill.getPostage() != null) {
            this.postage.setText(bill.getPostage().toString());
        }
        if (bill.getNumber() != null) {
            this.number.setText(String.valueOf(bill.getNumber()));
        }

        initBillEntryTableB(bill);
        calculateAndSetSums();
    }

    List<BillEntry> getBillEntries() {
        return billEntries.getItems();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.postage.textProperty().addListener((observable, oldValue, newValue) -> calculateAndSetSums());

        TextFields.bindAutoCompletion(this.customer,
                this.customerService.listAll().stream().map(customer1 -> customer1.getCompany() + " - " + customer1.getCompanyAddition()).collect(Collectors.toList()));

        this.customer.textProperty().addListener((observable, oldValue, newValue) -> {
            Customer customer = findCustomer();

            if (customer != null) {
                Bill lastBill = customer.getBills().stream().reduce((a, b) -> b).orElse(null);
                int nextBillNumber = 1;
                if (lastBill != null) {
                    if (!lastBill.getDate().isAfter((LocalDate.now()))) {
                        nextBillNumber = lastBill.getNumber() + 1;
                    }
                }
                this.number.setText(String.valueOf(nextBillNumber));
            }
        });


    }


    private void initBillEntryTableB(Bill bill) {

        this.positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        this.amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        this.periodColumn.setCellValueFactory(tableCell -> new SimpleStringProperty(DateUtil.formatToDisplayDate(tableCell.getValue().getPeriod())));


        this.addBillEntry.setOnAction(e -> {
            BillEntry billEntry = new BillEntry(bill);
            billEntry.setPosition(position.getText());
            billEntry.setPeriod(period.getValue());
            try {
                billEntry.setAmount(new BigDecimal(MoneyUtil.convertToGermanCurrency(amount.getText())));
            } catch (NumberFormatException nfe) {
                // Ignore: It is handled due to validate fields of the bill entry
            }
            try {
                billEntry.validateFields();
                this.billEntries.getItems().add(billEntry);
                calculateAndSetSums();
                this.position.clear();
                this.period.setValue(null);
            } catch (BusinessException e1) {
                errorLabel.setText(e1.getMessage());
            }

        });


        this.billEntryActionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        this.billEntryActionColumn.setPrefWidth(100);
        this.billEntryActionColumn.setCellFactory(param -> new TableCell<BillEntry, BillEntry>() {


            @Override
            protected void updateItem(final BillEntry entity, final boolean empty) {
                super.updateItem(entity, empty);

                if (entity == null) {
                    setGraphic(null);
                    return;
                }

                final Button deleteButton = new Button();
                final FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                deleteIcon.setIcon(FontAwesomeIconName.TRASH);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("button");
                deleteButton.setOnAction(event -> {
                    billEntries.getItems().remove(entity);
                    calculateAndSetSums();
                });

                setGraphic(new HBox(15, deleteButton));

            }
        });


        calculateAndSetSums();
    }

    private void calculateAndSetSums() {
        BigDecimal amount = this.billEntries.getItems().stream().map(BillEntry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal vat = amount.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(19));
        BigDecimal subtotal = amount.add(vat);
        BigDecimal sum = subtotal.add(MoneyUtil.convertToBigDecimal(postage.getText()));

        this.amountTotal.setText(MoneyUtil.toCurrencyWithSymbol(amount));
        this.vat.setText(MoneyUtil.toCurrencyWithSymbol(vat));
        this.subTotal.setText(MoneyUtil.toCurrencyWithSymbol(subtotal));
        this.total.setText(MoneyUtil.toCurrencyWithSymbol(sum));
    }

    int getNumber() {
        return Integer.valueOf(this.number.getText());
    }

    LocalDate getDate() {
        return this.date.getValue();
    }

    public Customer getCustomer() {
        return findCustomer();
    }

    private Customer findCustomer() {
        final String[] customerInputText = this.customer.getText().split(" - ");

        if (customerInputText.length != 2) {
            return null;
        }
        return this.customerService.findByCompany(customerInputText[0]);
    }

    BigDecimal getAmount() {
        return MoneyUtil.convertToBigDecimal(amountTotal.getText());
    }

    BigDecimal getVat() {
        return MoneyUtil.convertToBigDecimal(vat.getText());
    }

    BigDecimal getSubTotal() {
        return MoneyUtil.convertToBigDecimal(subTotal.getText());
    }

    BigDecimal getPostage() {
        return MoneyUtil.convertToBigDecimal(postage.getText());
    }

    BigDecimal getTotal() {
        return MoneyUtil.convertToBigDecimal(total.getText());
    }
}
