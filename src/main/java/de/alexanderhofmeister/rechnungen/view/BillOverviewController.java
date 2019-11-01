package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.model.Properties;
import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.util.ButtonUtil;
import de.alexanderhofmeister.rechnungen.util.DateUtil;
import de.alexanderhofmeister.rechnungen.util.ExportUtil;
import de.alexanderhofmeister.rechnungen.util.MoneyUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillOverviewController extends EntityOverviewController<Bill, BillEditController> {


    private BillService service = new BillService();

    @Override
    protected String getEditViewFileName() {
        return "editBill";
    }

    @Override
    protected String getFilterCountNamedQuery() {
        return Bill.NQ_COUNT_FILTER;
    }

    @Override
    protected BillService getService() {
        return this.service;
    }

    @Override
    protected String getFilterNamedQuery() {
        return Bill.NQ_FILTER;
    }

    @Override
    protected List<TableColumn<Bill, ?>> getEntityColumns() {

        TableColumn<Bill, String> number = new TableColumn<>("Nummer");
        number.setPrefWidth(75);

        TableColumn<Bill, String> customer = new TableColumn<>("Kunde");
        customer.setPrefWidth(300);

        TableColumn<Bill, String> total = new TableColumn<>("Betrag");
        total.setPrefWidth(100);

        TableColumn<Bill, String> date = new TableColumn<>("Datum");
        total.setPrefWidth(100);


        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        customer.setCellValueFactory(tableCell -> {
            Customer valueCustomer = tableCell.getValue().customer;
            return new SimpleStringProperty(valueCustomer.company + " - " + valueCustomer.companyAddition);
        });
        date.setCellValueFactory(tableCell -> new SimpleStringProperty(DateUtil.formatToDisplayDate(tableCell.getValue().date)));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));

        return Arrays.asList(customer, number, total, date);
    }


    @Override
    List<Button> getCustomButtons(Bill bill) {
        final Button exportAsPdf = ButtonUtil.createIconButton(event -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("customer", bill.customer);
            attributes.put("bill", bill);
            attributes.put("MoneyUtil", MoneyUtil.class);
            attributes.put("DateUtil", DateUtil.class);
            attributes.put("Properties", Properties.getInstance());
            ExportUtil.createFileFromTemplate(bill.date, new File(ExportUtil.getFileNameBill(bill)), "bill", attributes);
        }, FontAwesomeIconName.
                SAVE, "Speichern");
        final Button printButton = ButtonUtil.createIconButton(event1 -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("customer", bill.customer);
            attributes.put("bill", bill);
            attributes.put("MoneyUtil", MoneyUtil.class);
            attributes.put("DateUtil", DateUtil.class);
            attributes.put("Properties", Properties.getInstance());
            ExportUtil.printFile(ExportUtil.createFileFromTemplate(bill.date, new File(ExportUtil.getFileNameBill(bill)), "bill", attributes));
        }, FontAwesomeIconName.PRINT, "Drucken");


        final Button emailButton = ButtonUtil.createIconButton(event -> {

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("bill", bill);
            try {
                String body = ExportUtil.fillTemplateFromVariables("emailBill", attributes);
                Map<String, Object> attributes1 = new HashMap<>();
                attributes1.put("customer", bill.customer);
                attributes1.put("bill", bill);
                attributes1.put("MoneyUtil", MoneyUtil.class);
                attributes1.put("DateUtil", DateUtil.class);
                attributes1.put("Properties", Properties.getInstance());
                ExportUtil.sendViaEmail(ExportUtil.createFileFromTemplate(bill.date, new File(ExportUtil.getFileNameBill(bill)), "bill", attributes1), bill.toString(), body, bill.customer.email);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, FontAwesomeIconName.SEND, "Als E-Mail senden");


        return Arrays.asList(exportAsPdf, printButton, emailButton);
    }

    @Override
    protected void mapEditEntity(Bill bill, BillEditController controller) {
        bill.number = controller.getNumber();
        bill.date = controller.getDate();
        bill.customer = controller.getCustomer();
        bill.amount = controller.getAmount();
        bill.vat = controller.getVat();
        bill.subtotal = controller.getSubTotal();
        bill.postage = controller.getPostage();
        bill.total = controller.getTotal();
        bill.entries = controller.getBillEntries();
    }
}
