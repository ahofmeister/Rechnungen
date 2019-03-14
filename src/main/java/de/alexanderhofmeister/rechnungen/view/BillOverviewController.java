package de.alexanderhofmeister.rechnungen.view;

import com.itextpdf.html2pdf.HtmlConverter;
import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Properties;
import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.util.DateUtil;
import de.alexanderhofmeister.rechnungen.util.FxmlUtil;
import de.alexanderhofmeister.rechnungen.util.MoneyUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
    private TableColumn<Bill, LocalDate> date;

    @FXML
    private TableColumn<Bill, Bill> action;

    @FXML
    private TextField filterField;

    @FXML
    private Button newBill;

    private BillService billService = new BillService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {


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
        this.date.setCellValueFactory(new PropertyValueFactory<>("date"));
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
            protected void updateItem(final Bill entity, final boolean empty) {
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
                editButton.setOnAction(event -> loadBillEdit(entity));

                final Button deleteButton = new Button();
                final FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                deleteIcon.setIcon(FontAwesomeIconName.TRASH);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("button");
                deleteButton.setOnAction(event -> billService.delete(entity));

                final Button exportAsPdf = new Button();
                final FontAwesomeIcon pdfIcon = new FontAwesomeIcon();
                pdfIcon.setIcon(FontAwesomeIconName.FILE);
                exportAsPdf.setGraphic(pdfIcon);
                exportAsPdf.getStyleClass().add("button");
                exportAsPdf.setOnAction(event -> {

                    final StringWriter stringWriter = new StringWriter();


                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("customer", entity.getCustomer());
                    attributes.put("bill", entity);
                    attributes.put("MoneyUtil", MoneyUtil.class);
                    attributes.put("DateUtil", DateUtil.class);
                    attributes.put("Properties", Properties.getInstance());
                    final VelocityContext context = new VelocityContext(attributes);

                    try {
                        Velocity.evaluate(context, stringWriter, "PackageTemplatesVelocity",
                                FileUtils.readFileToString(new File("./templates/bill.html"), StandardCharsets.ISO_8859_1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        HtmlConverter.convertToPdf(stringWriter.toString(), new FileOutputStream("a.pdf"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

                setGraphic(new HBox(15, editButton, deleteButton, exportAsPdf));

            }
        });

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
            } catch (BusinessException e) {
                ae.consume();
                controller.setErrorText(e.getMessage());
            }

        });

    }
}
